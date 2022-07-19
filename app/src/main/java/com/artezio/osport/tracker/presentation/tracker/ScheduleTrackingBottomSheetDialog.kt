package com.artezio.osport.tracker.presentation.tracker

import android.app.*
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.ScheduleTrackingBottomDialogLayoutBinding
import com.artezio.osport.tracker.domain.model.Event
import com.artezio.osport.tracker.util.HOUR_IN_MILLIS
import com.artezio.osport.tracker.util.convertHoursOrMinutesToMilliseconds
import com.artezio.osport.tracker.util.convertMillisTo
import com.artezio.osport.tracker.util.millisecondsToDateFormat
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
    private var dateEnd: Long = 0
    private var eventName: String = ""
    private var eventId: Long = 0L
    private var alreadyExists: Boolean = false

    private val viewModel: TrackerViewModel by viewModels()
    private var pickedTime: Long = 0L

    private val binding: ScheduleTrackingBottomDialogLayoutBinding by lazy {
        val inflater = LayoutInflater.from(requireContext())
        ScheduleTrackingBottomDialogLayoutBinding.inflate(inflater)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        eventId = arguments?.getLong("eventId") ?: 0L
        eventName = arguments?.getString("eventName") ?: ""
        dateStart = arguments?.getLong("startDate") ?: 0L
        dateEnd = arguments?.getLong("endDate") ?: 0L
        alreadyExists = arguments?.getBoolean("exists") ?: false

        if (alreadyExists) {
            binding.bottomSheetDialogTitle.text =
                getString(R.string.schedule_tracking_bottom_sheet_title_edit)
        }

        setValues(eventName, dateStart, dateEnd)

        binding.eventNameTIL.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                eventName = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}

        })
        binding.buttonStart.setOnClickListener {
            pick(binding.buttonStart)
        }
        binding.buttonFinish.setOnClickListener {
            pick(binding.buttonFinish)
        }
        binding.buttonSchedule.setOnClickListener {
            if (alreadyExists) {
                generateEvent()
            } else {
                updateEvent()
            }

        }
        binding.buttonCancel.setOnClickListener {
            this.dismiss()
        }
        return BottomSheetDialog(requireContext(), theme).apply {
            setContentView(binding.root)
        }
    }

    private fun setValues(name: String, startDate: Long, endDate: Long) {
        if (name.isNotEmpty()) binding.eventNameTIL.editText?.setText(name)
        if (startDate != 0L) binding.buttonStart.text = millisecondsToDateFormat(startDate)
        if (endDate != 0L) binding.buttonFinish.text = millisecondsToDateFormat(endDate)
    }

    private fun updateEvent() {
        val event = Event(
            eventName,
            dateStart,
            dateEnd
        )
        viewModel.updateEvent(eventId, event)
    }


    private fun generateEvent() {
        if (dateStart != 0L && dateEnd != 0L) {
            viewModel.generateEvent(
                isPlanned = true,
                eventName = eventName,
                startDate = dateStart
            )
            Log.d("eventId", eventId.toString())
//            TrackerSchedulerLauncher.schedule(
//                requireContext(),
//                eventId + 1,
//                dateStart,
//                dateEnd,
//                eventName,
//            )
            this.dismiss()
            Toast.makeText(
                requireContext(),
                getString(R.string.train_planned_text),
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.time_start_and_finish_recording_warning),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun pick(view: Button) {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setTitleText("Выберите дату")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        picker.addOnPositiveButtonClickListener {
            pickedTime = it
            pickTime(dateStart, view)
            picker.dismiss()
        }
        picker.show(childFragmentManager, "date_picker")
    }

    private fun pickTime(startDate: Long, view: Button) {
        val picker = MaterialTimePicker.Builder()
            .setHour(convertMillisTo(startDate, Calendar.HOUR))
            .setMinute(convertMillisTo(startDate, Calendar.MINUTE))
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setPositiveButtonText("Ок")
            .setNegativeButtonText("Отмена")
            .build()
        picker.addOnPositiveButtonClickListener {
            pickedTime += (convertHoursOrMinutesToMilliseconds(
                picker.hour,
                Calendar.HOUR
            ) + convertHoursOrMinutesToMilliseconds(
                picker.minute,
                Calendar.MINUTE
            )) - 3 * HOUR_IN_MILLIS
            when (view.id) {
                binding.buttonStart.id -> {
                    binding.buttonStart.text = millisecondsToDateFormat(pickedTime)
                    dateStart = pickedTime
                }
                binding.buttonFinish.id -> {
                    binding.buttonFinish.text = millisecondsToDateFormat(pickedTime)
                    dateEnd = pickedTime
                }
                else -> {}
            }
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