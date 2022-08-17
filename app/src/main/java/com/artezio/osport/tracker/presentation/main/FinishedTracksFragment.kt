package com.artezio.osport.tracker.presentation.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.artezio.osport.tracker.databinding.FragmentFinishedTracksBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.presentation.TrackService
import com.artezio.osport.tracker.presentation.main.recycler.finished_events.EventsRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FinishedTracksFragment : BaseFragment<FragmentFinishedTracksBinding, MainViewModel>(),
    IFragment {

    override val viewModel: MainViewModel by viewModels()

    private val eventsAdapter: EventsRecyclerAdapter by lazy {
        EventsRecyclerAdapter(this)
    }

    private var currentRunningEventId: Long = -1L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewFinishedTracks.apply {
            adapter = eventsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        TrackService.currentEventIdLiveData.observe(viewLifecycleOwner) {
            Log.d("current_last_event_id", "currentEventIdLiveData.observe: $it")
            currentRunningEventId = it
        }

        TrackService.serviceLifecycleState.observe(viewLifecycleOwner) { state ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.eventsWithDataFlow.collect { events ->
                    if (currentRunningEventId != -1L) {
                        eventsAdapter.list = if (events.size == 1) {
                            emptyList()
                        } else {
                            events.filter { it.id != currentRunningEventId }
                        }
                    } else {
                        eventsAdapter.list = events
                    }
                }
            }
        }
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFinishedTracksBinding {
        return FragmentFinishedTracksBinding.inflate(inflater, container, false)
    }
}