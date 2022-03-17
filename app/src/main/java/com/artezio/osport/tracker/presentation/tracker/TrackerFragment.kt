package com.artezio.osport.tracker.presentation.tracker

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.FragmentTrackerBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TrackerFragment : BaseFragment<FragmentTrackerBinding>()
//    OnMapReadyCallback,
//    GoogleMap.OnMyLocationButtonClickListener
{

    override var bottomNavigationViewVisibility = View.GONE

    private val viewModel: TrackerViewModel by viewModels()
//    private lateinit var googleMap: GoogleMap

    private val fusedLocationProvider: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = 1000L
        fastestInterval = 1000L
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private var eventId: Long = -1L

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            val currentLocation = result.lastLocation
            Log.d("tracker_accuracy", "Accuracy: ${currentLocation.accuracy}")
//            viewModel.animateCamera(
//                googleMap,
//                LatLng(currentLocation.latitude, currentLocation.longitude)
//            )
            val accuracy = currentLocation.accuracy
            val detectedAccuracy = viewModel.detectAccuracy(accuracy)
//            binding.textViewAccuracyValue.apply {
//                text = accuracy.toString()
//                setTextColor(
//                    ContextCompat.getColor(
//                        requireContext(),
//                        detectedAccuracy.second.color
//                    )
//                )
//            }
        }
    }

    private val navigateBackOptions: NavOptions =
        NavOptions.Builder()
            .setPopExitAnim(R.anim.from_top_to_bottom_animation)
            .build()


    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        initMap(savedInstanceState)
        viewModel.observeServiceStateInTrackerFragment(viewLifecycleOwner, binding)

//        binding.fabStartTracking.setOnClickListener {
////            googleMap.clear()
//            fusedLocationProvider.removeLocationUpdates(locationCallback)
//            viewModel.generateEvent()
//            startService()
//        }
//
//        binding.fabStopTracking.setOnClickListener {
//            viewModel.stopService(requireContext())
//            fusedLocationProvider.requestLocationUpdates(
//                locationRequest,
//                locationCallback,
//                Looper.getMainLooper()
//            )
//        }
//
//        binding.fabToTrackerStatistics.setOnClickListener {
//            findNavController().navigate(
//                R.id.action_trackerFragment_to_trackerStatisticsFragment,
//                TrackerStatisticsFragmentArgs(eventId).toBundle()
//            )
//        }

        binding.buttonClose.setOnClickListener {
            findNavController().navigate(R.id.action_trackerFragment_to_mainFragment)
        }
        observeUserLocation()
    }

    @SuppressLint("MissingPermission")
    private fun observeUserLocation() {
        fusedLocationProvider.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun startService() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.lastEventIdFlow.collectLatest { lastEventId ->
                    eventId = lastEventId
                    viewModel.startService(requireContext(), lastEventId)
                    viewModel.getLocationsByEventId(lastEventId).collect { locations ->
                        if (locations.isNotEmpty()) {
                            val lastLocation = locations[locations.size - 1]
//                            viewModel.animateCamera(
//                                googleMap,
//                                LatLng(lastLocation.first.latitude, lastLocation.first.longitude)
//                            )
                            binding.textViewAccuracyValue.apply {
                                val lastLocationText = "${lastLocation.first.accuracy}м."
                                text = lastLocationText
                                val textColor = lastLocation.second.color
                                setTextColor(ContextCompat.getColor(requireContext(), textColor))
                            }
                        }
//                        viewModel.buildRoute(locations, googleMap)
                    }
                }
            }
        }
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackerBinding =
        FragmentTrackerBinding.inflate(inflater, container, false)
//
//    @SuppressLint("MissingPermission")
//    override fun onMapReady(map: GoogleMap) {
//        map.isMyLocationEnabled = true
//        map.setOnMyLocationButtonClickListener(this)
//        map.mapType = GoogleMap.MAP_TYPE_HYBRID
//        map.mapType = GoogleMap.MAP_TYPE_NORMAL
//        map.mapType = GoogleMap.MAP_TYPE_SATELLITE
//        map.mapType = GoogleMap.MAP_TYPE_TERRAIN
//        googleMap = map
//    }
//
//    private fun initMap(savedInstanceState: Bundle?) {
//        binding.mapView.onCreate(savedInstanceState)
//        binding.mapView.onResume()
//        binding.mapView.getMapAsync(this)
//    }
//
//    override fun onMyLocationButtonClick(): Boolean {
//        return false
//    }


}
