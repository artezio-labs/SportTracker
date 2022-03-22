package com.artezio.osport.tracker.presentation.event

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.FragmentEventInfoBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventInfoFragment : BaseFragment<FragmentEventInfoBinding>() {

    override var bottomNavigationViewVisibility = View.GONE

    private val viewModel: EventInfoViewModel by viewModels()
    private val navArgs: EventInfoFragmentArgs by navArgs()

    private var title = ""
    private var time = 0.0
    private var speed = 0.0
    private var distance = 0.0
    private var tempo = 0.0
    private var steps = 0
    private var gpsPoints = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonClose.setOnClickListener {
            findNavController().navigate(R.id.action_eventInfoFragment_to_mainFragment)
        }
        val locations = viewModel.getDistanceByEventId(navArgs.eventId)
        Log.d("event_info", navArgs.eventId.toString())
        viewModel.getEventById(navArgs.eventId)
        viewModel.eventLiveData.observe(viewLifecycleOwner) { event ->
            Log.d("event_info", "Event from livedata: $event")
            title = event.name
            time = event.timerValue
            speed = event.speedValue
            distance = locations.first
            tempo = (distance / (time / 60.0 + (time % 60.0) / 60.0))
            steps = event.stepsValue
            gpsPoints = event.gpsPointsValue

            binding.EventTitle.text = title
            binding.materialTextViewTimeValue.text = viewModel.formatTime(time)
            binding.materialTextViewSpeedValue.text = String.format("%.2f", speed)
            binding.materialTextViewDistanceValue.text = String.format("%.2f", distance)
            binding.materialTextViewTempoValue.text = String.format("%.2f", tempo)
            binding.materialTextViewStepsValue.text = steps.toString()
            binding.materialTextViewGPSValue.text = gpsPoints.toString()
        }
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEventInfoBinding =
        FragmentEventInfoBinding.inflate(inflater, container, false)
}