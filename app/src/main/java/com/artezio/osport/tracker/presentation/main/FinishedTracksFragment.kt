package com.artezio.osport.tracker.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.artezio.osport.tracker.databinding.FragmentFinishedTracksBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.presentation.main.recycler.EventsRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FinishedTracksFragment : BaseFragment<FragmentFinishedTracksBinding, MainViewModel>(), IFragment {

    override val viewModel: MainViewModel by viewModels()

    private val eventsAdapter: EventsRecyclerAdapter by lazy {
        EventsRecyclerAdapter(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewFinishedTracks.apply {
            adapter = eventsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventsWithDataFlow.collect {
                eventsAdapter.list = it
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