package com.artezio.osport.tracker.presentation.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.artezio.osport.tracker.databinding.FragmentPlannedTracksBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlannedTracksFragment : BaseFragment<FragmentPlannedTracksBinding, MainViewModel>() {

    override val viewModel: MainViewModel by viewModels()

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlannedTracksBinding {
        return FragmentPlannedTracksBinding.inflate(inflater, container, false)
    }
}