package com.artezio.sporttracker.presentation.tracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Camera
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.artezio.sporttracker.data.prefs.PrefsManager
import com.artezio.sporttracker.data.trackservice.TrackService
import com.artezio.sporttracker.databinding.FragmentTrackerBinding
import com.artezio.sporttracker.presentation.BaseFragment
import com.artezio.sporttracker.util.START_FOREGROUND_SERVICE
import com.artezio.sporttracker.util.STOP_FOREGROUND_SERVICE
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackerFragment : BaseFragment<FragmentTrackerBinding>(), OnMapReadyCallback {

    private val viewModel: TrackerViewModel by viewModels()

    private lateinit var googleMap: GoogleMap

    @Inject
    lateinit var prefsManager: PrefsManager

    private var isServiceRunning = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap(savedInstanceState)

        binding.fabStartTracking.setOnClickListener {
            googleMap.clear()
            viewModel.generateEvent()
            val job = viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.lastEventIdFlow.cancellable().collectLatest { lastEventId ->
                        val intent = Intent(requireActivity(), TrackService::class.java).apply {
                            putExtra("eventId", lastEventId)
                            action = START_FOREGROUND_SERVICE
                        }
                        requireActivity().startService(intent)
                        viewModel.getLocationsByEventId(lastEventId).cancellable().collect { locations ->
                            Log.d("steps", "Locationslist: $locations")
                            Log.d("steps", "Last event id: $lastEventId")
                            if(locations.isNotEmpty()) {
                                googleMap.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        locations[locations.size - 1],
                                        40F
                                    )
                                )
                            }

                            viewModel.buildRoute(locations, googleMap)
                        }
                    }

                }
            }
            job.cancel()
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

    private val isRunning = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            isServiceRunning = true
        }
    }

    companion object {
        const val START = "START"
        const val STOP = "STOP"
    }

}