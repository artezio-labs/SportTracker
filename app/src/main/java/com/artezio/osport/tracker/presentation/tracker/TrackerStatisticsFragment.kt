package com.artezio.osport.tracker.presentation.tracker

import android.annotation.SuppressLint
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
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.prefs.PrefsManager
import com.artezio.osport.tracker.databinding.FragmentTrackerStatisticsBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.presentation.TrackService
import com.artezio.osport.tracker.presentation.main.MainFragmentArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class TrackerStatisticsFragment : BaseFragment<FragmentTrackerStatisticsBinding>(),
    OnMapReadyCallback {

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

    private var lastLocation = LatLng(0.0, 0.0)

    @Inject
    lateinit var prefsManager: PrefsManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap(savedInstanceState)
        viewModel.observeServiceStateWhenPaused(viewLifecycleOwner, binding)
        viewModel.timerValueLiveData.observe(viewLifecycleOwner) { timerValue ->
            Log.d("timer_value", "timer value: $timerValue")
            time = timerValue
            binding.textViewTimerValue.text = viewModel.getTimerStringFromDouble(timerValue)
            if (binding.materialCardViewPausedStatisticsCard.visibility == View.VISIBLE) {
                Log.d("timer_when_paused", "Timer value: $time $timerValue")
                binding.timerWhenPausedValue.text = viewModel.getTimerStringFromDouble(timerValue)
                binding.pauseDistanceValue.text = String.format("%.2f", distance)
                binding.pauseAverageSpeedValue.text = String.format("%.2f", averageSpeed)
                binding.pauseTempoValue.text = String.format("%.2f", tempoValue)
                binding.pauseStepsValue.text = steps.toString()
                binding.pauseGpsCountValue.text = gpsPoints.toString()
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
                    }
                    if (binding.mapStatistics.visibility == View.VISIBLE
                        && locations.isNotEmpty()) {
                        val lastLocation = locations.last()
                        zoomCamera(
                            LatLng(
                                lastLocation.first.latitude,
                                lastLocation.first.longitude
                            )
                        )
                    }
                }
            }
        }
    }

    private val stepsBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val stepCount = intent?.getIntExtra(TrackService.STEPS_EXTRA, 0)
            if (stepCount != null) {
                steps = stepCount
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
        viewModel.currentFragmentIdLiveData.postValue(R.id.trackerStatisticsFragment4)
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

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        map.isMyLocationEnabled = true
        map.mapType = GoogleMap.MAP_TYPE_HYBRID
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        map.mapType = GoogleMap.MAP_TYPE_SATELLITE
        map.mapType = GoogleMap.MAP_TYPE_TERRAIN
        googleMap = map
    }

    private fun initMap(savedInstanceState: Bundle?) {
        binding.mapStatistics.onCreate(savedInstanceState)
        binding.mapStatistics.onResume()
        binding.mapStatistics.getMapAsync(this)
    }

    private fun zoomCamera(latLng: LatLng) {
        val cameraPosition = CameraUpdateFactory.newLatLngZoom(latLng, 16F)
        googleMap.moveCamera(cameraPosition)
    }
}