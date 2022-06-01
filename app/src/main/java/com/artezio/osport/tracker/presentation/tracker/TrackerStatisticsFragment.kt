package com.artezio.osport.tracker.presentation.tracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.FragmentTrackerStatisticsBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.util.MapUtils
import com.artezio.osport.tracker.util.getTimerStringFromDouble
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TrackerStatisticsFragment :
    BaseFragment<FragmentTrackerStatisticsBinding, TrackerViewModel>() {

    override var bottomNavigationViewVisibility = View.GONE
    override val viewModel: TrackerViewModel by viewModels()

    private var time = 0.0
    private var averageSpeed = 0.0
    private var distance = 0.0
    private var tempoValue = 0.0
    private var gpsPoints = 0
    private var pauseCadence = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeServiceStateWhenPaused(viewLifecycleOwner, binding)
        viewModel.timerValueLiveData.observe(viewLifecycleOwner) { timerValue ->
            Log.d("timer_value", "timer value: $timerValue")
            time = timerValue
            binding.textViewTimerValue.text = getTimerStringFromDouble(timerValue)
            if (binding.materialCardViewPausedStatisticsCard.visibility == View.VISIBLE) {
                Log.d("timer_when_paused", "Timer value: $time $timerValue")
                binding.timerWhenPausedValue.text = getTimerStringFromDouble(timerValue)
            }
        }
        binding.buttonClose.setOnClickListener {
            requireActivity().findNavController(R.id.fragmentContainerView)
                .navigate(
                    R.id.action_sessionRecordingFragment_to_mainFragment
                )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getLocationsByEventId().collect { points ->
                if (points.isNotEmpty() && binding.mapStatistics.isVisible) {
                    MapUtils.drawRoute(requireContext(), binding.mapStatistics, points)
                }
            }
        }
        observeData()
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.lastEventIdFlow.collectLatest { lastEventId ->
                viewModel.getLocationsByEventIdWithAccuracy(lastEventId).collect { locations ->
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

                        if (binding.materialCardViewPausedStatisticsCard.visibility == View.VISIBLE) {
                            binding.pauseDistanceValue.text = String.format("%.2f", distance)
                            binding.pauseAverageSpeedValue.text =
                                String.format("%.2f", averageSpeed)
                            binding.pauseTempoValue.text = String.format("%.2f", tempoValue)
                            binding.pauseGpsCountValue.text = gpsPoints.toString()
                            binding.pauseCadenceValue.text = pauseCadence.toString()
                        }
                    }
                }
            }
        }
        viewModel.stepsLiveData.observe(viewLifecycleOwner) { stepsCount ->
            Log.d("steps", "Steps from livedata")
            val stepsString = stepsCount.toString()
            binding.textViewStepsValue.text = stepsString
            if (binding.materialCardViewPausedStatisticsCard.visibility == View.VISIBLE) {
                binding.pauseStepsValue.text = stepsString
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pedometerDataForCadence.collectLatest { data ->
                val cadence = viewModel.calculateCadence(data)
                binding.textViewCadenceValue.text = cadence.toString()
                pauseCadence = cadence
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.currentFragmentIdLiveData.postValue(R.id.trackerStatisticsFragment4)
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackerStatisticsBinding =
        FragmentTrackerStatisticsBinding.inflate(inflater, container, false)
}