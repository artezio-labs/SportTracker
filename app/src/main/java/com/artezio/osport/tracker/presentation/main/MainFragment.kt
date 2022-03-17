package com.artezio.osport.tracker.presentation.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.artezio.osport.tracker.databinding.FragmentMainBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.presentation.main.recycler.EventsRecyclerAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(), IFragment {
    private val viewModel: MainViewModel by viewModels()
    private val eventsAdapter: EventsRecyclerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        EventsRecyclerAdapter(this)
    }

    private val fusedLocationProvider: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("events", "onViewCreated: ")

        binding.eventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventsAdapter
        }
        binding.fabAddEvent.setOnClickListener {
//            if (hasLocationAndActivityRecordingPermission(requireContext())) {
//                val locationTask = fusedLocationProvider.lastLocation
//                locationTask.addOnSuccessListener { location ->
//                    if (location != null) {
//                        findNavController().navigate(
//                            R.id.action_mainFragment_to_trackerFragment
//                        )
//                    }
//                }
//            } else {
//                requestLocationPermissions(this)
//                requestPhysicalActivityPermissions(this)
//            }
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
