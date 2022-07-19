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
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.trackservice.ServiceLifecycleState
import com.artezio.osport.tracker.databinding.FragmentSessionRecordingBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.presentation.TrackService
import com.artezio.osport.tracker.util.DialogBuilder
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SessionRecordingFragment : BaseFragment<FragmentSessionRecordingBinding, TrackerViewModel>() {

    override var bottomNavigationViewVisibility = View.GONE

    override val viewModel: TrackerViewModel by viewModels()

    override var onBackPressed: Boolean = false

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

        requestLocationUpdates()

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
            TrackService.timerValueLiveData.value?.let {
                if (it > 10) {
                    viewModel.stopService(requireContext())
                    viewModel.navigateToSaveEventFragment(eventId)
                } else {
                    showFinishTrackingWarningDialog()
                }
            }

        }

        binding.fabStart.setOnClickListener {
            fusedLocationProvider.removeLocationUpdates(locationCallback)
            viewModel.generateEvent(isPlanned = false)
            startService()
        }

        binding.fabStopTracking.setOnClickListener {
            viewModel.stopService(requireContext())
            try {
                requestLocationUpdates()
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

    private fun showFinishTrackingWarningDialog() {
        DialogBuilder(
            context = requireContext(),
            title = getString(R.string.warning_dialogs_title),
            message = getString(R.string.finish_tracking_warning_dialog_text),
            positiveButtonText = getString(R.string.dialog_continue_text),
            positiveButtonClick = { dialog, _ ->
                viewModel.resumeTracking(requireContext())
                dialog.dismiss()
            },
            negativeButtonText = getString(R.string.dialog_cancel_button_text),
            negativeButtonClick = { dialog, _ ->
                viewModel.deleteLastEvent()
                viewModel.navigateBack()
                viewModel.stopService(requireContext())
                dialog.dismiss()
            },
            needsToShow = true
        ).build()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fusedLocationProvider.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        if (TrackService.serviceLifecycleState.value == ServiceLifecycleState.NOT_STARTED) {
            fusedLocationProvider.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
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