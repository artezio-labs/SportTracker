package com.artezio.osport.tracker.presentation.main

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.FragmentMainBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.presentation.event.EventCreateAndUpdateFragmentArgs
import com.artezio.osport.tracker.presentation.main.recycler.EventsRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(), IFragment {
    private val viewModel: MainViewModel by viewModels()
    private val eventsAdapter: EventsRecyclerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        EventsRecyclerAdapter(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("events", "onViewCreated: ")
        binding.eventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventsAdapter
        }
        binding.fabAddEvent.setOnClickListener {
            val args = EventCreateAndUpdateFragmentArgs(eventId = -1L, title = "Создать")
            findNavController().navigate(
                R.id.action_mainFragment_to_trackerFragment
            )
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventsWithDataFlow.collect {
                    eventsAdapter.list = viewModel.buildListOfEvents(it)
                }
            }
        }
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMainBinding =
        FragmentMainBinding.inflate(inflater, container, false)
}
