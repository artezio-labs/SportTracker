package com.artezio.sporttracker.presentation.statistics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.artezio.sporttracker.R
import com.artezio.sporttracker.databinding.FragmentStatisticsBinding
import com.artezio.sporttracker.presentation.BaseFragment
import com.artezio.sporttracker.presentation.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : BaseFragment<FragmentStatisticsBinding>() {

    private val viewModel: StatisticsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentStatisticsBinding {
        return FragmentStatisticsBinding.inflate(inflater, container, false)
    }
}