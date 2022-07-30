package com.artezio.osport.tracker.presentation.tracker

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.ScheduleTrackingBottomDialogLayoutBinding
import com.artezio.osport.tracker.domain.model.PlannedEvent
import com.artezio.osport.tracker.presentation.tracker.shedule.TrackerSchedulerLauncher
import com.artezio.osport.tracker.util.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class ScheduleTrackingBottomSheetDialog : BottomSheetDialogFragment() {

    private var dateStart: Long = 0
    private var duration: Int = 0
    private var eventName: String = ""
    private var calibrationTime: Int = 0
    private var eventId: Long = 0L
    private var alreadyExists: Boolean = false

    private val viewModel: TrackerViewModel by viewModels()
    private var pickedDate: Long = -1L
    private var pickedTime: Long = 0L

    private val binding: ScheduleTrackingBottomDialogLayoutBinding by lazy {
        val inflater = LayoutInflater.from(requireContext())
        ScheduleTrackingBottomDialogLayoutBinding.inflate(inflater)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        eventId = arguments?.getLong("eventId") ?: -1L
        Log.d("planner_worker_states", "Event id in dialog: $eventId")
        eventName = arguments?.getString("eventName") ?: ""
        dateStart =
            arguments?.getLong("startDate") ?: (getCurrentTimeMillis() + 5 * MINUTE_IN_MILLIS)
        duration = arguments?.getInt("duration") ?: 120
        calibrationTime = arguments?.getInt("calibration") ?: 60
        alreadyExists = arguments?.getBoolean("exists") ?: false

        if (alreadyExists) {
            binding.bottomSheetDialogTitle.text =
                getString(R.string.schedule_tracking_bottom_sheet_title_edit)
            binding.buttonSchedule.text =
                getString(R.string.schedule_tracking_bottom_sheet_button_update_text)
        } else {
            binding.eventNameTIL.editText?.setText(
                millisecondsToDateFormatForPlanning(
                    getCurrentTimeMillis() + 5 * MINUTE_IN_MILLIS
                )
            )
        }

        setValues(eventName, dateStart, duration)
        calculateStartTime()

        binding.eventNameTIL.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                eventName = p0.toString()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                eventName = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                eventName = p0.toString()
            }

        })

        binding.buttonDuration.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val durationString = p0.toString()
                if (durationString.isNotBlankOrEmpty()) {
                    duration = durationString.toInt()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })
        binding.buttonCalibrationTime.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val calibrationTimeString = p0.toString()
                if (calibrationTimeString.isNotBlankOrEmpty()) {
                    calibrationTime = calibrationTimeString.toInt()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })
        binding.buttonDateEditText.setOnClickListener {
            pickDate()
        }
        binding.buttonTimeEditText.setOnClickListener {
            pickTime(dateStart)
        }
        binding.buttonSchedule.setOnClickListener {
            if (validateInputs()) {
                if (alreadyExists) {
                    updateEvent()
                } else {
                    generateEvent()
                }
            }
            Log.d("picker_date", "start date: $dateStart")
        }
        binding.buttonCancel.setOnClickListener {
            this.dismiss()
        }
        return BottomSheetDialog(requireContext(), theme).apply {
            setContentView(binding.root)
            setCanceledOnTouchOutside(false)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun calculateStartTime() {
        dateStart = getMillisFromDateString(binding.buttonDate.editText?.editableText.toString()) +
                getMillisFromString(binding.buttonTime.editText?.editableText.toString())
        Log.d(
            "pick_time",
            "Millis: $dateStart, date: ${millisecondsToDateFormatForPlanning(dateStart)}"
        )
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        if (binding.eventNameTIL.editText?.editableText.toString().isBlankOrEmpty()) {
            binding.eventNameTIL.error = "Поле не должно быть пустым!"
            isValid = false
        }
        if (binding.buttonDate.editText?.editableText.toString().isBlankOrEmpty()) {
            binding.buttonDate.error = "Поле не должно быть пустым!"
            isValid = false
        }
        if (binding.buttonTime.editText?.editableText.toString().isBlankOrEmpty()) {
            binding.buttonTime.error = "Поле не должно быть пустым!"
            isValid = false
        }
        if (binding.buttonDuration.editText?.editableText.toString().isBlankOrEmpty()) {
            binding.buttonDuration.error = "Поле не должно быть пустым!"
            isValid = false
        }
        if (binding.buttonCalibrationTime.editText?.editableText.toString().isBlankOrEmpty()) {
            binding.buttonCalibrationTime.error = "Поле не должно быть пустым!"
            isValid = false
        }

        if (binding.buttonDuration.editText?.editableText.toString().isNotBlankOrEmpty()) {
            if (binding.buttonDuration.editText?.editableText.toString().toInt() !in 5..1440) {
                binding.buttonDuration.error = "Длительность должна быть от 5 до 1440 минут!"
                isValid = false
            }
        }

        if (binding.buttonCalibrationTime.editText?.editableText.toString().isNotBlankOrEmpty()) {
            if (binding.buttonCalibrationTime.editText?.editableText.toString()
                    .toInt() !in 10..600
            ) {
                binding.buttonCalibrationTime.error =
                    "Допустимое время калибровки от 10 до 600 секунд!"
                isValid = false
            }
        }
        return isValid
    }

    private fun setValues(name: String, startDate: Long, duration: Int) {
        if (name.isNotEmpty()) binding.eventNameTIL.editText?.setText(name)
        if (startDate != 0L) {
            val date = formatEventName(startDate)
            Log.d("pick_time", "Date: $date")
            binding.buttonDate.editText?.setText(formatEventName(startDate))
            binding.buttonTime.editText?.setText(getTimeFromMillis(startDate))
        } else {
            val date = formatEventName(getCurrentTimeMillis())
            Log.d("pick_time", "Date: $date")
            binding.buttonDate.editText?.setText(formatEventName(getCurrentTimeMillis()))
            binding.buttonTime.editText?.setText(getTimeFromMillis(getCurrentTimeMillis() + 5 * MINUTE_IN_MILLIS))
        }
        if (duration != 0) binding.buttonDuration.editText?.setText(duration.toString())
        if (calibrationTime != 0) binding.buttonCalibrationTime.editText?.setText(calibrationTime.toString())
    }

    private fun updateEvent() {
        val name = binding.eventNameTIL.editText?.editableText.toString()
        val event = PlannedEvent(
            if (viewModel.validateString(
                    name,
                    StringValidationPattern.DATE
                )
            ) "${binding.buttonDate.editText?.editableText.toString()} ${binding.buttonTime.editText?.editableText.toString()}" else name,
            dateStart,
            duration,
        )
        Log.d("planner_worker_states", "updateEvent: $event \n Event id before update: $eventId")
        viewModel.updateEvent(eventId, event)
        TrackerSchedulerLauncher.schedule(
            requireContext(),
            eventId + 1,
            dateStart,
            duration,
            calibrationTime,
            eventName,
        )
        this.dismiss()
    }

    private fun generateEvent() {
        val eventNameDate = binding.buttonDate.editText?.editableText.toString()
        val name = binding.eventNameTIL.editText?.editableText.toString()
        lifecycleScope.launch {
            val period = dateStart..dateStart + duration * MINUTE_IN_MILLIS
            Log.d("has_union", "$period")
            viewModel.checkScheduledTrainingForPeriod(dateStart, duration)
                .asFlow()
                .collectLatest { hasIntersections ->
                    if (hasIntersections) {
                        showHasIntersectionsDialog()
                    } else {
                        viewModel.generatePlannedEvent(
                            if (name.matches("^([1-9]|([012][0-9])|(3[01]))-([0]{0,1}[1-9]|1[012])-\\d\\d\\d\\d [012]{0,1}[0-9]:[0-6][0-9]\$")) "$eventNameDate ${
                                getTimeFromMillis(
                                    dateStart
                                )
                            }" else name,
                            dateStart,
                            duration,
                            calibrationTime
                        )
                        viewModel.generateEvent(
                            if (name.matches("^([1-9]|([012][0-9])|(3[01]))-([0]{0,1}[1-9]|1[012])-\\d\\d\\d\\d [012]{0,1}[0-9]:[0-6][0-9]\$")) "$eventNameDate ${
                                getTimeFromMillis(
                                    dateStart
                                )
                            }" else name,
                            startDate = dateStart
                        )
                        Log.d("eventId", eventId.toString())
                        TrackerSchedulerLauncher.schedule(
                            requireContext(),
                            eventId + 1,
                            dateStart,
                            duration,
                            calibrationTime,
                            eventName
                        )
                        this@ScheduleTrackingBottomSheetDialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.train_planned_text),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun showHasIntersectionsDialog() {
        DialogBuilder(
            context = requireContext(),
            title = "Внимание",
            message = "Период, который вы выбрали пересекается с другими запланированными тренировками. Выберите другую дату старта или длительность.",
            positiveButtonText = "Ок",
            positiveButtonClick = { dialog, _ -> dialog.cancel() },
            needsToShow = true
        ).build()
    }

    private fun pickDate() {
        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()
        val picker = MaterialDatePicker.Builder.datePicker()
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setTitleText("Выберите дату")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraints)
            .build()
        picker.addOnPositiveButtonClickListener {
            Log.d("pick_time", "pickDate: $it")
            if (it < getCurrentTimeMillis()) {
                binding.buttonDate.error = "Дата начала не может быть меньше текущей!"
            } else {
                val date = formatEventName(it)
                Log.d("pick_time", "Date: $date")
                binding.buttonDateEditText.setText(date)
                pickedDate = it
                calculateStartTime()
                picker.dismiss()
            }
        }
        picker.show(childFragmentManager, "date_picker")
    }

    private fun pickTime(startDate: Long) {
        var pickerTime = 0L
        val timeFromTextField =
            if (binding.buttonTime.editText?.editableText.toString().isNotBlankOrEmpty()) {
                binding.buttonTime.editText?.editableText.toString().split(":").map { it.toInt() }
            } else {
                pickerTime = startDate.ifZero { getCurrentTimeMillis(plus = HOUR_IN_MILLIS) }
                mutableListOf(
                    convertMillisTo(pickerTime, Calendar.HOUR_OF_DAY),
                    convertMillisTo(pickerTime, Calendar.MINUTE)
                )
            }
        Log.d("pick_time", "pickTime: $pickerTime")
        val picker = MaterialTimePicker.Builder().apply {
            setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            setHour(timeFromTextField[0])
            setMinute(timeFromTextField[1])
            setTimeFormat(TimeFormat.CLOCK_24H)
            setPositiveButtonText("Ок")
            setNegativeButtonText("Отмена")
        }.build()
        picker.addOnPositiveButtonClickListener {
            pickedTime = convertHoursOrMinutesToMilliseconds(
                picker.hour,
                Calendar.HOUR_OF_DAY
            ) + convertHoursOrMinutesToMilliseconds(
                picker.minute,
                Calendar.MINUTE
            ) - 3 * HOUR_IN_MILLIS
            val timeFromMillis = getTimeFromMillis(pickedTime)
            Log.d("pick_time", "pickTime from millis: $timeFromMillis")
            if (!validateTime(timeFromMillis)) {
                binding.buttonTime.isErrorEnabled = true
                binding.buttonTime.error = "Выбранное время не может быть меньше текущего!"
                Log.d(
                    "pick_time",
                    "pickTime start: ${getTimeFromMillis(pickedTime)} current time: ${getCurrentTimeMillis()} diff: ${
                        getTimeFromMillis(
                            pickedDate + pickedTime - (getCurrentTimeMillis())
                        )
                    }"
                )
            } else {
                binding.buttonTime.error = null
                binding.buttonTime.isErrorEnabled = false
                binding.buttonTime.editText?.setText(getTimeFromMillis(pickedTime))
                binding.buttonTime.editText?.error = null
                calculateStartTime()
                Log.d("pick_time", "Date start final: ${formatEventName(dateStart)}")
                picker.dismiss()
            }
        }
        picker.show(childFragmentManager, "time_picker")
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    companion object {
        const val TAG = "ScheduleTrackingBottomSheetDialog"
        const val START_NOTIFICATION_CODE = 7539
        const val FINISH_NOTIFICATION_CODE = 2957
    }
}