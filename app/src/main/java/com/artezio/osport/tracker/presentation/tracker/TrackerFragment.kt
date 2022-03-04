package com.artezio.osport.tracker.presentation.tracker

import android.annotation.SuppressLint
import android.app.Dialog
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
import androidx.navigation.fragment.findNavController
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.FragmentTrackerBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.util.DialogBuilder
import com.artezio.osport.tracker.util.hasLocationAndActivityRecordingPermission
import com.artezio.osport.tracker.util.requestLocationPermission
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class TrackerFragment : BaseFragment<FragmentTrackerBinding>(),
    OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener {

    private val viewModel: TrackerViewModel by viewModels()
    private lateinit var googleMap: GoogleMap

    private var eventId: Long = -1L

    private var progressDialog: Dialog? = null

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            val currentLocation = result.lastLocation
            Log.d("tracker_accuracy", "Accuracy: ${currentLocation.accuracy}")
            viewModel.animateCamera(
                googleMap,
                LatLng(currentLocation.latitude, currentLocation.longitude)
            )
            progressDialog?.let {
                if (currentLocation.accuracy <= 5) {
                    if(it.isShowing) it.dismiss()
                }
            }
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap(savedInstanceState)
        viewModel.observeServiceStateInTrackerFragment(viewLifecycleOwner, binding)
        if (!hasLocationAndActivityRecordingPermission(requireContext())) {
            showInfoDialog()
        }

        binding.fabStartTracking.setOnClickListener {
            googleMap.clear()
            viewModel.generateEvent()
            if (hasLocationAndActivityRecordingPermission(requireContext())) {
                startService()
            } else {
                requestLocationPermission(this)
            }
        }

        binding.fabStopTracking.setOnClickListener {
            viewModel.stopService(requireContext())
        }

        binding.fabToTrackerStatistics.setOnClickListener {
            findNavController().navigate(
                R.id.action_trackerFragment_to_trackerStatisticsFragment,
                TrackerStatisticsFragmentArgs(eventId).toBundle()
            )
        }

        observeUserLocation()
        progressDialog = DialogBuilder(requireContext(), layoutId = R.layout.progress_dialog_layout)
            .build()

        val timer = Timer()
        timer.schedule(object: TimerTask() {
            override fun run() {
                if(progressDialog?.isShowing == true) progressDialog?.dismiss()
                timer.cancel()
            }
        }, 30 * 1000)
    }

    private fun observeUserLocation() {
        val fusedLocationProvider: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        val locationRequest = LocationRequest.create().apply {
            interval = 1000L
            fastestInterval = 1000L
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
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
                            viewModel.animateCamera(
                                googleMap,
                                LatLng(lastLocation.first.latitude, lastLocation.first.longitude)
                            )
                            binding.textViewAccuracyValue.apply {
                                val lastLocationText = "${lastLocation.first.accuracy}м."
                                text = lastLocationText
                                val textColor = lastLocation.second.color
                                setTextColor(ContextCompat.getColor(requireContext(), textColor))
                            }
                        }
                        viewModel.buildRoute(locations, googleMap)
                    }
                }
            }
        }
    }

    private fun showInfoDialog() {
        DialogBuilder(
            context = requireContext(),
            title = getString(R.string.info_dialog_title_text),
            message = getString(R.string.info_dialog_message_text),
            negativeButtonText = "Ок",
            negativeButtonClick = { dialog, _ -> dialog.cancel() }
        ).build()
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
