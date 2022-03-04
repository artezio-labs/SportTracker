package com.artezio.osport.tracker.presentation.event

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.FragmentSaveEventBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.presentation.tracker.TrackerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SaveEventFragment : BaseFragment<FragmentSaveEventBinding>() {

    private val saveEventViewModel: SaveEventViewModel by viewModels()
    private val trackerViewModel: TrackerViewModel by viewModels()
    private var id: Long = -1L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSaveEvent.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        val trackingState = trackerViewModel.getTrackingState()
                        val eventName = binding.eventNameTIL.editText?.editableText.toString()
                        Log.d("event_save", trackingState.toString() + "\n$eventName")
                        saveEventViewModel.lastEventId.collectLatest { eventId ->
                            id = eventId
                            if (eventId != -1L) {
                                saveEventViewModel.updateEvent(eventId, eventName, trackingState!!)
                            }
                        }
                }
            }
            navigate()
        }

        binding.buttonDeleteEvent.setOnClickListener {
            saveEventViewModel.deleteEvent(id)
            navigate()
        }
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSaveEventBinding = FragmentSaveEventBinding.inflate(inflater, container, false)

    private fun navigate() {
        findNavController().navigate(R.id.action_saveEventFragment_to_mainFragment)
    }
}