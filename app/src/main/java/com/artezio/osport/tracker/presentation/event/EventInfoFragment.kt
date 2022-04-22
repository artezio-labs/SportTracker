package com.artezio.osport.tracker.presentation.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.FragmentEventInfoBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.util.MapUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventInfoFragment : BaseFragment<FragmentEventInfoBinding>() {

    override var bottomNavigationViewVisibility = View.GONE

    private val viewModel: EventInfoViewModel by viewModels()
    private val navArgs: EventInfoFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonClose.setOnClickListener {
            findNavController().navigate(R.id.action_eventInfoFragment_to_mainFragment)
        }
        val id = navArgs.eventId

        viewModel.getLocationsById(id)
        viewModel.locationsLiveData.observe(viewLifecycleOwner) { locations ->
            MapUtils.drawRoute(requireContext(), binding.eventInfoMap, locations)
        }

        viewModel.getEventInfo(id)
        viewModel.eventInfoLiveData.observe(viewLifecycleOwner) { eventInfo ->
            binding.EventTitle.text = eventInfo.title
            binding.materialTextViewTimeValue.text = eventInfo.time
            binding.materialTextViewDistanceValue.text = eventInfo.distance
            binding.materialTextViewSpeedValue.text = eventInfo.speed
            binding.materialTextViewTempoValue.text = eventInfo.tempo
            binding.materialTextViewStepsValue.text = eventInfo.steps
            binding.materialTextViewGPSValue.text = eventInfo.gpsPoints
        }

    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEventInfoBinding =
        FragmentEventInfoBinding.inflate(inflater, container, false)
}