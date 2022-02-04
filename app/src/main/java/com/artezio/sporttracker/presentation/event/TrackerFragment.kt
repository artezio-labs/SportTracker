package com.artezio.sporttracker.presentation.event

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.artezio.sporttracker.R
import com.artezio.sporttracker.databinding.FragmentTrackerBinding
import com.artezio.sporttracker.presentation.BaseFragment

class TrackerFragment : BaseFragment<FragmentTrackerBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackerBinding =
        FragmentTrackerBinding.inflate(inflater, container, false)

}