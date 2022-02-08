package com.artezio.sporttracker.presentation.tracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.artezio.sporttracker.data.trackservice.TrackService
import com.artezio.sporttracker.databinding.FragmentTrackerBinding
import com.artezio.sporttracker.presentation.BaseFragment
import com.artezio.sporttracker.util.START_FOREGROUND_SERVICE
import com.artezio.sporttracker.util.STOP_FOREGROUND_SERVICE
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TrackerFragment : BaseFragment<FragmentTrackerBinding>(), OnMapReadyCallback {

    private val viewModel: TrackerViewModel by viewModels()

    private lateinit var googleMap: GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap(savedInstanceState)

        binding.fabStartTracking.setOnClickListener {
            Log.d("steps", "Start button clicked")
            googleMap.clear()
            viewModel.generateEvent()
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.lastEventIdFlow.collectLatest { lastEventId ->
                        val intent = Intent(requireActivity(), TrackService::class.java).apply {
                            putExtra("eventId", lastEventId)
                            action = START_FOREGROUND_SERVICE
                        }
                        requireActivity().startService(intent)
                        viewModel.getLocationsByEventId(lastEventId).collect { locations ->
                            Log.d("steps", "Locationslist: $locations")
                            Log.d("steps", "Last event id: $lastEventId")
                            if (locations.isNotEmpty()) {
                                googleMap.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        locations[locations.size - 1],
                                        23F
                                    )
                                )
                            }
                            viewModel.buildRoute(locations, googleMap)
                        }
                    }
                }
            }
            binding.fabStart.visibility = View.GONE
            binding.fabStop.visibility = View.VISIBLE
        }

        binding.fabStopTracking.setOnClickListener {
            val intent = Intent(requireActivity(), TrackService::class.java).apply {
                action = STOP_FOREGROUND_SERVICE
            }
            requireActivity().stopService(intent)
            binding.fabStop.visibility = View.GONE
            binding.fabStart.visibility = View.VISIBLE
        }
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackerBinding =
        FragmentTrackerBinding.inflate(inflater, container, false)

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    private fun initMap(savedInstanceState: Bundle?) {
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.onResume()
        binding.mapView.getMapAsync(this)
    }
}