package com.artezio.osport.tracker.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.artezio.osport.tracker.databinding.FragmentPlannedTracksBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.presentation.main.recycler.planned_events.PlannedEventsRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlannedTracksFragment : BaseFragment<FragmentPlannedTracksBinding, MainViewModel>(), IFragment {

    override val viewModel: MainViewModel by viewModels()

    private var plannedEventsRecyclerAdapter: PlannedEventsRecyclerAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        plannedEventsRecyclerAdapter = PlannedEventsRecyclerAdapter(this,viewModel::deletePlannedEvent)

        binding.recyclerViewPlannedTracks.apply {
            adapter = plannedEventsRecyclerAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.plannedEventsFlow.collect {
                plannedEventsRecyclerAdapter?.list = it
            }
        }
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlannedTracksBinding {
        return FragmentPlannedTracksBinding.inflate(inflater, container, false)
    }
}