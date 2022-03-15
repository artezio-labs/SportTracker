package com.artezio.osport.tracker.presentation.tracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.prefs.PrefsManager
import com.artezio.osport.tracker.databinding.FragmentTrackerStatisticsBinding
import com.artezio.osport.tracker.domain.model.TrackingStateModel
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.presentation.TrackService
import com.artezio.osport.tracker.presentation.event.SaveEventFragmentArgs
import com.artezio.osport.tracker.util.PAUSE_FOREGROUND_SERVICE
import com.artezio.osport.tracker.util.RESUME_FOREGROUND_SERVICE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class TrackerStatisticsFragment : BaseFragment<FragmentTrackerStatisticsBinding>() {

    private val viewModel: TrackerViewModel by viewModels()

    private val args: TrackerStatisticsFragmentArgs by navArgs()

    private var time = 0.0
    private var averageSpeed = 0.0
    private var distance = 0.0
    private var tempoValue = 0.0
    private var steps = 0
    private var gpsPoints = 0

    private var trackerState: TrackingStateModel? = null

    @Inject
    lateinit var prefsManager: PrefsManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.timerValueLiveData.observe(viewLifecycleOwner) { timerValue ->
            Log.d("timer_value", "timer value: $timerValue")
            time = timerValue
            binding.textViewTimerValue.text = viewModel.getTimerStringFromDouble(timerValue)
        }

        binding.fabToTrackerMap.setOnClickListener {
            findNavController().navigate(R.id.action_trackerStatisticsFragment_to_trackerFragment)
        }

        binding.fabStopPause.setOnClickListener {
            trackerState = TrackingStateModel(
                timerValue = time,
                speedValue = averageSpeed,
                distanceValue = distance,
                tempoValue = tempoValue,
                stepsValue = steps,
                gpsPointsValue = gpsPoints
            )
            Log.d("timer_value", "tracker state: $trackerState")
            prefsManager.trackingState = trackerState!!
            prefsManager.steps = steps
            it.visibility = View.GONE
            binding.llFabs.visibility = View.VISIBLE
            pauseTracking()
        }

        binding.fabFinishTracking.setOnClickListener {
            val eventId = args.eventId
            findNavController().navigate(
                R.id.action_trackerStatisticsFragment_to_saveEventFragment,
                SaveEventFragmentArgs(eventId).toBundle()
            )
            viewModel.stopService(requireContext())
            trackerState?.let {
                Log.d("event_save", "State before saving: $it")
                prefsManager.trackingState = it
            }
        }

        binding.fabResumeTracking.setOnClickListener {
            Log.d("track_timer", "fab clicked")
            binding.llFabs.visibility = View.GONE
            binding.fabStopPause.visibility = View.VISIBLE
            resumeTracking()
        }
        observeData()
    }

    private fun pauseTracking() {
        val intent = Intent(requireContext(), TrackService::class.java).apply {
            action = PAUSE_FOREGROUND_SERVICE
        }
        requireContext().startService(intent)
    }

    private fun resumeTracking() {
        val intent = Intent(requireContext(), TrackService::class.java).apply {
            action = RESUME_FOREGROUND_SERVICE
        }
        requireContext().startService(intent)
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.lastEventIdFlow.collectLatest { lastEventId ->
                viewModel.getLocationsByEventId(lastEventId).collect { locations ->
                    if (locations.isNotEmpty()) {
                        averageSpeed = locations.map { it.first.speed * 3.6 }.average()
                        binding.textViewAverageSpeedValue.text =
                            String.format(
                                "%.2f",
                                averageSpeed
                            )
                        distance = viewModel.calculateDistance(locations)
                        binding.textViewDistanceValue.text = String.format("%.2f", distance)
                        val minutes = time / 60.0 + (time % 60.0) / 60.0
                        tempoValue = if (distance != 0.0) minutes / distance else 0.0
                        binding.textTempoPerKilometerValue.text =
                            String.format("%.2f", tempoValue)
                        gpsPoints = locations.size
                        binding.textViewGpsPointsValue.text = gpsPoints.toString()
                    }
                }
            }
        }
    }

    private val stepsBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val stepCount = intent?.getIntExtra(TrackService.STEPS_EXTRA, 0)
            if (stepCount != null) {
                binding.textViewStepsValue.text = stepCount.toString()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        requireContext().registerReceiver(
            stepsBroadcastReceiver,
            IntentFilter(TrackService.STEPS_UPDATED)
        )
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(stepsBroadcastReceiver)
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackerStatisticsBinding =
        FragmentTrackerStatisticsBinding.inflate(inflater, container, false)
}