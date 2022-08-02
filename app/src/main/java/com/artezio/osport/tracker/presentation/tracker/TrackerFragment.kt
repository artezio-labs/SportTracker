package com.artezio.osport.tracker.presentation.tracker

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.trackservice.ServiceLifecycleState
import com.artezio.osport.tracker.databinding.FragmentTrackerBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.presentation.TrackService
import com.artezio.osport.tracker.util.MapUtils
import com.google.android.gms.location.*
import com.mapbox.geojson.Point
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackerFragment : BaseFragment<FragmentTrackerBinding, TrackerViewModel>(), LocationListener {

    override var bottomNavigationViewVisibility = View.GONE

    override val viewModel: TrackerViewModel by viewModels()

    private val fusedLocationProvider: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = 1000L
        fastestInterval = 1000L
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private var location: Location? = null

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            location = result.lastLocation
        }
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestLocationsUpdate()
        binding.buttonClose.setOnClickListener {
            requireActivity().findNavController(R.id.fragmentContainerView)
                .navigateUp()
        }
        MapUtils.initMap(binding.mapView)
        binding.buttonFindMe.setOnClickListener {
            if (location != null) {
                MapUtils.setMapCamera(binding.mapView.getMapboxMap(), location!!)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationsUpdate() {
        if (TrackService.serviceLifecycleState.value != ServiceLifecycleState.CALIBRATING ||
            TrackService.serviceLifecycleState.value != ServiceLifecycleState.RUNNING) {
            fusedLocationProvider.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackerBinding =
        FragmentTrackerBinding.inflate(inflater, container, false)

    override fun onDestroy() {
        super.onDestroy()
        MapUtils.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fusedLocationProvider.removeLocationUpdates(locationCallback)
    }

    override fun onLocationChanged(p0: Location) {
        location = p0
    }
}
