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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.trackservice.ServiceLifecycleState
import com.artezio.osport.tracker.databinding.FragmentSessionRecordingBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.presentation.TrackService
import com.artezio.osport.tracker.presentation.event.SaveEventFragmentArgs
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SessionRecordingFragment : BaseFragment<FragmentSessionRecordingBinding>() {

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

    private var eventId: Long = -1L

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            val currentLocation = result.lastLocation
            Log.d("tracker_accuracy", "Accuracy: ${currentLocation.accuracy}")
            val calculatedAccuracyPair = viewModel.calculateAccuracy(currentLocation)
            binding.accuracyValue.text = calculatedAccuracyPair.first
            binding.accuracyValue.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    calculatedAccuracyPair.second.color
                )
            )
        }
    }

    private val childFragmentNavController: NavController by lazy {
        val childNavHostFragment =
            childFragmentManager.findFragmentById(R.id.fcvSessionRecording) as NavHostFragment
        childNavHostFragment.navController
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeServiceState(viewLifecycleOwner, binding)

        requestLocaionUpdates()

        binding.fabStart.setOnClickListener {
            Log.d("service_state", "Fab clicked, service is started")
            startService()
        }

        binding.fabStopPause.setOnClickListener {
            binding.llFabs.visibility = View.VISIBLE
            viewModel.pauseTracking(requireContext())
        }

        binding.fabToSessionStatistics.setOnClickListener {
            it.visibility = View.GONE
            childFragmentNavController.navigate(R.id.action_trackerFragment3_to_trackerStatisticsFragment4)
            binding.fabToSessionMap.visibility = View.VISIBLE
        }

        binding.fabToSessionMap.setOnClickListener {
            it.visibility = View.GONE
            childFragmentNavController.navigate(R.id.action_trackerStatisticsFragment4_to_trackerFragment3)
            binding.fabToSessionStatistics.visibility = View.VISIBLE
        }

        binding.fabResumeTracking.setOnClickListener {
            viewModel.resumeTracking(requireContext())
        }

        binding.fabFinishTracking.setOnClickListener {
            viewModel.stopService(requireContext())
            findNavController().navigate(
                R.id.action_sessionRecordingFragment_to_saveEventFragment2,
                SaveEventFragmentArgs(eventId).toBundle()
            )
        }

        binding.fabStart.setOnClickListener {
            fusedLocationProvider.removeLocationUpdates(locationCallback)
            viewModel.generateEvent()
            startService()
        }

        binding.fabStopTracking.setOnClickListener {
            viewModel.stopService(requireContext())
            try {
                requestLocaionUpdates()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }

        viewModel.currentFragmentIdLiveData.observe(viewLifecycleOwner) { currentFragmentId ->
            when (currentFragmentId) {
                R.id.trackerFragment3 -> {
                    if (TrackService.serviceLifecycleState.value != ServiceLifecycleState.NOT_STARTED) {
                        binding.fabToSessionStatistics.visibility = View.VISIBLE
                        binding.fabToSessionMap.visibility = View.GONE
                    }
                }
                R.id.trackerStatisticsFragment4 -> {
                    if (TrackService.serviceLifecycleState.value != ServiceLifecycleState.NOT_STARTED) {
                        binding.fabToSessionMap.visibility = View.VISIBLE
                        binding.fabToSessionStatistics.visibility = View.GONE
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocaionUpdates() {
        fusedLocationProvider.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun startService() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.lastEventIdFlow.collectLatest { lastEventId ->
                eventId = lastEventId
                viewModel.startService(requireContext(), lastEventId)
            }
        }
        childFragmentNavController.navigate(R.id.action_trackerFragment3_to_trackerStatisticsFragment4)

        binding.fabToSessionMap.visibility = View.VISIBLE
        binding.fabToSessionStatistics.visibility = View.GONE
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSessionRecordingBinding =
        FragmentSessionRecordingBinding.inflate(inflater, container, false)
}