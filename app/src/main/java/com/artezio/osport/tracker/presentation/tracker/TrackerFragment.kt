package com.artezio.osport.tracker.presentation.tracker

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.FragmentTrackerBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.presentation.main.MainFragmentArgs
import com.artezio.osport.tracker.util.MapUtils
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackerFragment : BaseFragment<FragmentTrackerBinding>() {

    override var bottomNavigationViewVisibility = View.GONE

    private val viewModel: TrackerViewModel by viewModels()

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
        }
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonClose.setOnClickListener {
            requireActivity().findNavController(R.id.fragmentContainerView)
                .navigate(
                    R.id.action_sessionRecordingFragment_to_mainFragment,
                    MainFragmentArgs(true).toBundle()
                )
        }
        MapUtils.initMap(binding.mapView)
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

    override fun onDestroy() {
        super.onDestroy()
        MapUtils.onDestroy()
    }
}
