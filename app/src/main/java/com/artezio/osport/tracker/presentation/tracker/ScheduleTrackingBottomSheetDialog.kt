package com.artezio.osport.tracker.presentation.tracker

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.ScheduleTrackingBottomDialogLayoutBinding
import com.artezio.osport.tracker.presentation.tracker.shedule.RecordingStartReceiver
import com.artezio.osport.tracker.presentation.tracker.shedule.TrackerScheduler
import com.artezio.osport.tracker.presentation.tracker.shedule.TrackerSchedulerLauncher
import com.artezio.osport.tracker.util.MINUTE
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ScheduleTrackingBottomSheetDialog : BottomSheetDialogFragment() {

    private var dateStart: Long = 0

    private val trackerScheduler: TrackerScheduler by lazy {
        TrackerScheduler(requireContext())
    }

    private val binding: ScheduleTrackingBottomDialogLayoutBinding by lazy {
        val inflater = LayoutInflater.from(requireContext())
        ScheduleTrackingBottomDialogLayoutBinding.inflate(inflater)
    }

    private val alarmManager: AlarmManager by lazy {
        requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding.buttonStart.setOnClickListener {
            setTime(binding.buttonStart)
        }
        binding.buttonSchedule.setOnClickListener {
            if ((dateStart != 0L)) {
//                trackerScheduler.schedule(dateStart)
                TrackerSchedulerLauncher.launch(requireContext(), dateStart)
                this.dismiss()
                Toast.makeText(requireContext(), getString(R.string.train_planned_text), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.time_start_and_finish_recording_warning),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return BottomSheetDialog(requireContext(), theme).apply {
            setContentView(binding.root)
        }
    }

    private fun setTime(view: Button) {
        Calendar.getInstance().apply {
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
            activity?.let {
                DatePickerDialog(
                    it,
                    0,
                    { _, year, month, dayOfMonth ->
                        this.set(Calendar.YEAR, year)
                        this.set(Calendar.MONTH, month)
                        this.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        TimePickerDialog(
                            it,
                            0,
                            { _, hour, min ->
                                this.set(Calendar.HOUR_OF_DAY, hour)
                                this.set(Calendar.MINUTE, min)
                                val dateFormatted = DateFormat.format("dd-MM-yyyy HH:mm", this)
                                view.text = dateFormatted
                                dateStart = this.timeInMillis
                                Log.d("bottom_nav", "Time: $dateStart")
                            },
                            this.get(Calendar.HOUR_OF_DAY),
                            this.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    this.get(Calendar.YEAR),
                    this.get(Calendar.MONTH),
                    this.get(Calendar.DAY_OF_MONTH)
                ).apply { datePicker.minDate = System.currentTimeMillis() }
                    .show()
            }
        }
    }

    private fun setStartAlarm() {
        val intent = Intent(requireContext(), RecordingStartReceiver::class.java).apply {
            putExtra("time", dateStart)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            START_NOTIFICATION_CODE,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, dateStart - MINUTE, pendingIntent)
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