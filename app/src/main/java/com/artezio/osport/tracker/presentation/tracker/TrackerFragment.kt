package com.artezio.osport.tracker.presentation.tracker

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.FragmentTrackerBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.presentation.main.MainFragmentArgs
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackerFragment : BaseFragment<FragmentTrackerBinding>(),
    OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener {

    override var bottomNavigationViewVisibility = View.GONE

    private val viewModel: TrackerViewModel by viewModels()
    private lateinit var googleMap: GoogleMap

    private val fusedLocationProvider: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = 1000L
        fastestInterval = 1000L
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            val currentLocation = result.lastLocation
            Log.d("tracker_accuracy", "Accuracy: ${currentLocation.accuracy}")
            viewModel.animateCamera(
                googleMap,
                LatLng(currentLocation.latitude, currentLocation.longitude)
            )
            val accuracy = currentLocation.accuracy
            val detectedAccuracy = viewModel.detectAccuracy(accuracy)
        }
    }

    private val navigateBackOptions: NavOptions =
        NavOptions.Builder()
            .setPopExitAnim(R.anim.from_top_to_bottom_animation)
            .build()


    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap(savedInstanceState)
        binding.buttonClose.setOnClickListener {
            requireActivity().findNavController(R.id.fragmentContainerView)
                .navigate(
                    R.id.action_sessionRecordingFragment_to_mainFragment,
                    MainFragmentArgs(true).toBundle()
                )
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

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackerBinding =
        FragmentTrackerBinding.inflate(inflater, container, false)

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        map.isMyLocationEnabled = true
        map.setOnMyLocationButtonClickListener(this)
        map.mapType = GoogleMap.MAP_TYPE_HYBRID
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        map.mapType = GoogleMap.MAP_TYPE_SATELLITE
        map.mapType = GoogleMap.MAP_TYPE_TERRAIN
        googleMap = map
    }

    private fun initMap(savedInstanceState: Bundle?) {
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.onResume()
        binding.mapView.getMapAsync(this)
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }
}
