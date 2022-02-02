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
import com.artezio.sporttracker.R
import com.artezio.sporttracker.data.trackservice.TrackService
import com.artezio.sporttracker.databinding.FragmentEventCreateAndUpdateBinding
import com.artezio.sporttracker.presentation.BaseFragment
import com.artezio.sporttracker.presentation.main.MainFragment
import com.artezio.sporttracker.util.dateToMilliseconds
import com.artezio.sporttracker.util.millisecondsToDateFormat
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class EventCreateAndUpdateFragment : BaseFragment<FragmentEventCreateAndUpdateBinding>() {

    private val viewModel: EventViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventIdArg = arguments?.getLong("eventId", -1L) ?: -1L
        val eventNameArg = arguments?.getString("eventName")
        val startDateArg = arguments?.getLong("eventStartDate")
        val endDateArg = arguments?.getLong("eventEndDate")

        if (!eventNameArg.isNullOrEmpty() && startDateArg != null && endDateArg != null) {
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
                viewModel.saveOrUpdateEvent(
                    eventIdArg,
                    eventName.editText?.editableText.toString(),
                    dateToMilliseconds(eventStartDate.toString())
                )
                parentFragmentManager.beginTransaction().apply {
                    setReorderingAllowed(true)
                    addToBackStack(null)
                    replace(R.id.fragmentContainerView, MainFragment())
                    commit()
                }
            } else
                Toast.makeText(
                    requireContext(),
                    "Все поля должны быть заполнены!",
                    Toast.LENGTH_SHORT
                ).show()
            scheduleStartService(dateToMilliseconds(eventStartDate.toString()))
        }

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

                                val dateFormatted = DateFormat.format("dd-MM-yyyy HH:mm", this)
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
        val pendingIntent = PendingIntent.getService(requireActivity(), 84573 , intent, 0)
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, date, pendingIntent)
        Log.d("steps", "scheduleStartService: ")
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEventCreateAndUpdateBinding =
        FragmentEventCreateAndUpdateBinding.inflate(inflater, container, false)


}