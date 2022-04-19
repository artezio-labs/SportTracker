package com.artezio.osport.tracker.presentation.tracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.prefs.PrefsManager
import com.artezio.osport.tracker.databinding.FragmentTrackerStatisticsBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.presentation.main.MainFragmentArgs
import com.artezio.osport.tracker.util.MapUtils
import com.artezio.osport.tracker.util.getTimerStringFromDouble
import com.google.android.gms.maps.GoogleMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class TrackerStatisticsFragment : BaseFragment<FragmentTrackerStatisticsBinding>() {

    private lateinit var googleMap: GoogleMap

    override var bottomNavigationViewVisibility = View.GONE
    private val viewModel: TrackerViewModel by viewModels()

    private val args: TrackerStatisticsFragmentArgs by navArgs()

    private var time = 0.0
    private var averageSpeed = 0.0
    private var distance = 0.0
    private var tempoValue = 0.0
    private var steps = 0
    private var gpsPoints = 0

    @Inject
    lateinit var prefsManager: PrefsManager

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
                    R.id.action_sessionRecordingFragment_to_mainFragment,
                    MainFragmentArgs(true).toBundle()
                )
        }
        observeData()
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

                        if (binding.materialCardViewPausedStatisticsCard.visibility == View.VISIBLE) {
                            binding.pauseDistanceValue.text = String.format("%.2f", distance)
                            binding.pauseAverageSpeedValue.text =
                                String.format("%.2f", averageSpeed)
                            binding.pauseTempoValue.text = String.format("%.2f", tempoValue)
                            binding.pauseGpsCountValue.text = gpsPoints.toString()
                        }
                    }
                    if (binding.mapStatistics.visibility == View.VISIBLE
                        && locations.isNotEmpty()
                    ) {
                        val lastLocation = locations.last()
                        MapUtils.initMap(binding.mapStatistics, lastLocation.first)
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