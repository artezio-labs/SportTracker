package com.artezio.osport.tracker.presentation.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.FragmentTrackerBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.util.DialogBuilder
import com.artezio.osport.tracker.util.hasLocationAndActivityRecordingPermission
import com.artezio.osport.tracker.util.requestLocationPermission
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TrackerFragment : BaseFragment<FragmentTrackerBinding>(), OnMapReadyCallback {

    private val viewModel: TrackerViewModel by viewModels()

    private lateinit var googleMap: GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap(savedInstanceState)
        viewModel.observeServiceState(viewLifecycleOwner, binding)
        if (!hasLocationAndActivityRecordingPermission(requireContext())) {
            showInfoDialog()
        }

        binding.fabStartTracking.setOnClickListener {
            googleMap.clear()
            viewModel.generateEvent()
            if (hasLocationAndActivityRecordingPermission(requireContext()))
                startService()
            else {
                requestLocationPermission(this)
            }
        }

        binding.fabStopTracking.setOnClickListener {
            viewModel.stopService(requireContext())
        }
    }

    private fun startService() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.lastEventIdFlow.collectLatest { lastEventId ->
                    viewModel.startService(requireContext(), lastEventId)
                    viewModel.getLocationsByEventId(lastEventId).collect { locations ->
                        if (locations.isNotEmpty()) {
                            viewModel.animateCamera(googleMap, locations[locations.size - 1])
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

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    private fun initMap(savedInstanceState: Bundle?) {
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.onResume()
        binding.mapView.getMapAsync(this)
    }
}
