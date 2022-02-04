package com.artezio.sporttracker.presentation.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.artezio.sporttracker.R
import com.artezio.sporttracker.databinding.FragmentMainBinding
import com.artezio.sporttracker.presentation.BaseFragment
import com.artezio.sporttracker.presentation.event.EventCreateAndUpdateFragment
import com.artezio.sporttracker.presentation.main.recycler.EventsRecyclerAdapter
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
            val args = bundleOf("eventId" to -1L)
            val fragment = EventCreateAndUpdateFragment().apply {
                arguments = args
            }
            parentFragmentManager.beginTransaction().apply {
                setReorderingAllowed(true)
                addToBackStack(null)
                replace(R.id.fragmentContainerView, fragment)
                commit()
            }
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

interface IFragment {
}