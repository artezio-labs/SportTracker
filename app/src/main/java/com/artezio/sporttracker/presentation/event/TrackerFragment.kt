package com.artezio.sporttracker.presentation.event

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.artezio.sporttracker.R
import com.artezio.sporttracker.databinding.FragmentTrackerBinding
import com.artezio.sporttracker.presentation.BaseFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackerFragment : BaseFragment<FragmentTrackerBinding>(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.onResume()
        binding.mapView.getMapAsync(this)
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackerBinding =
        FragmentTrackerBinding.inflate(inflater, container, false)

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

}