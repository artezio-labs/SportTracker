package com.artezio.osport.tracker.presentation.tracker

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.ScheduleTrackingBottomDialogLayoutBinding
import com.artezio.osport.tracker.domain.model.PlannedEvent
import com.artezio.osport.tracker.presentation.tracker.shedule.TrackerSchedulerLauncher
import com.artezio.osport.tracker.util.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
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
    private var pickedTime: Long = 0L

    private val binding: ScheduleTrackingBottomDialogLayoutBinding by lazy {
        val inflater = LayoutInflater.from(requireContext())
        ScheduleTrackingBottomDialogLayoutBinding.inflate(inflater)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        eventId = arguments?.getLong("eventId") ?: -1L
        Log.d("planner_worker_states", "Event id in dialog: $eventId")
        eventName = arguments?.getString("eventName") ?: ""
        dateStart = arguments?.getLong("startDate") ?: 0L
        duration = arguments?.getInt("duration") ?: 120
        calibrationTime = arguments?.getInt("calibration") ?: 1
        alreadyExists = arguments?.getBoolean("exists") ?: false

        if (alreadyExists) {
            binding.bottomSheetDialogTitle.text =
                getString(R.string.schedule_tracking_bottom_sheet_title_edit)
            binding.buttonSchedule.text =
                getString(R.string.schedule_tracking_bottom_sheet_button_update_text)
        } else {
            binding.eventNameTIL.editText?.setText(millisecondsToDateFormatForPlanning(System.currentTimeMillis()))
        }

        setValues(eventName, dateStart, duration)

        binding.eventNameTIL.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                eventName = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        binding.buttonDuration.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                duration = p0.toString().toInt()
            }

            override fun afterTextChanged(p0: Editable?) {}

        })
        binding.buttonCalibrationTime.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                calibrationTime = p0.toString().toInt()
            }

            override fun afterTextChanged(p0: Editable?) {}

        })
        binding.buttonStartEditText.setOnClickListener {
            pick()
        }
        binding.buttonSchedule.setOnClickListener {
            if (validateInputs()) {
                if (alreadyExists) {
                    updateEvent()
                } else {
                    generateEvent()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Проверьте правильность заполнения полей!",
                    Toast.LENGTH_SHORT
                ).show()
            }
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

    private fun validateInputs(): Boolean {
        return binding.eventNameTIL.editText?.text.toString()
            .isNotBlankOrEmpty() && viewModel.validateString(
            binding.buttonStart.editText?.text.toString(),
            StringValidationPattern.DATE
        ) && viewModel.validateString(
            binding.buttonDuration.editText?.text.toString(),
            StringValidationPattern.NUMBER
        ) && viewModel.validateString(
            binding.buttonCalibrationTime.editText?.text.toString(),
            StringValidationPattern.NUMBER
        )
    }


    private fun setValues(name: String, startDate: Long, duration: Int) {
        if (name.isNotEmpty()) binding.eventNameTIL.editText?.setText(name)
        if (startDate != 0L) binding.buttonStart.editText?.setText(
            millisecondsToDateFormatForPlanning(
                startDate
            )
        )
        if (duration != 0) binding.buttonDuration.editText?.setText(duration.toString())
        if (calibrationTime != 0) binding.buttonCalibrationTime.editText?.setText(calibrationTime.toString())
    }

    private fun updateEvent() {
        val event = PlannedEvent(
            eventName,
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
        viewModel.generatePlannedEvent(
            eventName,
            dateStart,
            duration,
            calibrationTime
        )
        viewModel.generateEvent(
            eventName = eventName,
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
        this.dismiss()
        Toast.makeText(
            requireContext(),
            getString(R.string.train_planned_text),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun pick() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setTitleText("Выберите дату")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        picker.addOnPositiveButtonClickListener {
            pickedTime = it - 3 * HOUR_IN_MILLIS
            if (pickedTime <= System.currentTimeMillis() - 24 * HOUR_IN_MILLIS) {
                Toast.makeText(
                    requireContext(),
                    "Дата начала не может быть меньше текущей!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                pickTime(dateStart)
                picker.dismiss()
            }
        }
        picker.show(childFragmentManager, "date_picker")
    }

    private fun pickTime(startDate: Long) {
        if (startDate != 0L && startDate < System.currentTimeMillis()) {
            Toast.makeText(
                requireContext(),
                "Время начала не может быть меньше текущего!",
                Toast.LENGTH_SHORT
            ).show()
        }
        val pickerTime = startDate.ifZero { System.currentTimeMillis() + 4 * HOUR_IN_MILLIS }
        val picker = MaterialTimePicker.Builder()
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .setHour(convertMillisTo(pickerTime, Calendar.HOUR))
            .setMinute(convertMillisTo(0L, Calendar.MINUTE))
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setPositiveButtonText("Ок")
            .setNegativeButtonText("Отмена")
            .build()
        picker.addOnPositiveButtonClickListener {
            pickedTime += convertHoursOrMinutesToMilliseconds(
                picker.hour,
                Calendar.HOUR
            ) + convertHoursOrMinutesToMilliseconds(
                picker.minute,
                Calendar.MINUTE
            )
            Log.d(
                "pick_time",
                "pickTime start: $pickedTime current time: ${System.currentTimeMillis()}"
            )
            binding.buttonStart.editText?.setText(millisecondsToDateFormatForPlanning(pickedTime))
            dateStart = pickedTime
            picker.dismiss()

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