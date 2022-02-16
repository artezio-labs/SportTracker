package com.artezio.sporttracker.presentation.event

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isNotEmpty
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.artezio.sporttracker.R
import com.artezio.sporttracker.data.trackservice.TrackService
import com.artezio.sporttracker.databinding.FragmentEventCreateAndUpdateBinding
import com.artezio.sporttracker.presentation.AlarmReceiver
import com.artezio.sporttracker.presentation.BaseFragment
import com.artezio.sporttracker.util.dateToMilliseconds
import com.artezio.sporttracker.util.millisecondsToDateFormat
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class EventCreateAndUpdateFragment : BaseFragment<FragmentEventCreateAndUpdateBinding>() {

    private val viewModel: EventViewModel by viewModels()

    private val args: EventCreateAndUpdateFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventIdArg = args.eventId
        val eventNameArg = args.eventName
        val startDateArg = args.eventStartDate
        val endDateArg = args.eventEndDate

        if (eventNameArg.isNotEmpty() && startDateArg != -1L) {
            binding.textInputLayoutInsertTitle.editText?.setText(eventNameArg)
            binding.textViewStartDate.text = millisecondsToDateFormat(startDateArg)
        }

        binding.buttonAddStartDate.setOnClickListener {
            pickTime()
        }

        binding.buttonSaveOrUpdateEvent.setOnClickListener {
            val eventName = binding.textInputLayoutInsertTitle
            val eventStartDate = binding.textViewStartDate.text
            if (eventName.isNotEmpty() && eventStartDate.isNotEmpty()) {
                val eventNameStr = eventName.editText?.editableText.toString()
                viewModel.saveOrUpdateEvent(
                    eventIdArg,
                    eventNameStr,
                    dateToMilliseconds(eventStartDate.toString())
                )
                setAlarm(
                    eventIdArg,
                    dateToMilliseconds(eventStartDate.toString()),
                    eventNameStr,
                    "Ивент $eventNameStr скоро начнется"
                )
                findNavController().navigate(R.id.action_eventCreateAndUpdateFragment_to_mainFragment)
            } else
                Toast.makeText(
                    requireContext(),
                    "Все поля должны быть заполнены!",
                    Toast.LENGTH_SHORT
                ).show()
//            scheduleStartService(dateToMilliseconds(eventStartDate.toString()))

        }

    }

    private fun setAlarm(
        eventId: Long,
        time: Long,
        title: String,
        description: String
    ) {
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra(TITLE_NAME, title)
            putExtra(CONTENT_TEXT, description)
            putExtra(EVENT_ID, eventId)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager: AlarmManager =
            context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        Log.d("steps", "alarm $eventId")
    }

    private fun pickTime() {
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

                                val dateFormatted = DateFormat.format("dd-MM-yyyy HH:mm:ss", this)
                                binding.textViewStartDate.text = dateFormatted
                            },
                            this.get(Calendar.HOUR_OF_DAY),
                            this.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    this.get(Calendar.YEAR),
                    this.get(Calendar.MONTH),
                    this.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
    }

    private fun scheduleStartService(date: Long) {
        val intent = Intent(requireActivity(), TrackService::class.java)
        val pendingIntent = PendingIntent.getService(requireActivity(), 84573, intent, 0)
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, date, pendingIntent)
        Log.d("steps", "scheduleStartService: ")
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEventCreateAndUpdateBinding =
        FragmentEventCreateAndUpdateBinding.inflate(inflater, container, false)


    companion object {
        private const val EVENT_ID = "EVENT_ID"
        private const val REMINDER_CHANNEL = "REMINDER_CHANNEL"
        private const val NOTIFICATION_ID = "NOTIFICATION_ID"
        private const val TITLE_NAME = "TITLE_NAME"
        private const val CONTENT_TEXT = "CONTENT_TEXT"
    }

}