package com.artezio.osport.tracker.presentation.tracker

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.FragmentTrackerBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.util.FusedLocationProviderClientDelegate
import com.artezio.osport.tracker.util.MapUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackerFragment : BaseFragment<FragmentTrackerBinding, TrackerViewModel>() {

    override var bottomNavigationViewVisibility = View.GONE

    override val viewModel: TrackerViewModel by viewModels()

    private val locationRequester: LocationRequester? by FusedLocationProviderClientDelegate(this)

    private var location: Location? = null

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (locationRequester != null) {
            locationRequester?.locationLiveData?.observe(viewLifecycleOwner) {
                location = it
            }
        }
        binding.buttonClose.setOnClickListener {
            requireActivity().findNavController(R.id.fragmentContainerView)
                .navigateUp()
        }
        MapUtils.initMap(binding.mapView)
        binding.buttonFindMe.setOnClickListener {
            location?.let {
                MapUtils.setMapCamera(binding.mapView.getMapboxMap(), it)
            }
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
}
