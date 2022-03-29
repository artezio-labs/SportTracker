package com.artezio.osport.tracker.presentation.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.trackservice.ServiceLifecycleState
import com.artezio.osport.tracker.databinding.FragmentSessionRecordingBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.presentation.TrackService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SessionRecordingFragment : BaseFragment<FragmentSessionRecordingBinding>() {

    override var bottomNavigationViewVisibility = View.GONE

    private val viewModel: TrackerViewModel by viewModels()

//    private val permissionsManager = SystemServicePermissionsManager(requireActivity() as MainActivity)

    private val childFragmentNavController: NavController by lazy {
        val childNavHostFragment =
            childFragmentManager.findFragmentById(R.id.fcvSessionRecording) as NavHostFragment
        childNavHostFragment.navController
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeServiceState(viewLifecycleOwner, binding)

        binding.fabStart.setOnClickListener {
            viewModel.startService(requireContext(), -1L)
            childFragmentNavController.navigate(R.id.action_trackerFragment3_to_trackerStatisticsFragment4)
        }

        binding.fabStopPause.setOnClickListener {
            binding.llFabs.visibility = View.VISIBLE
            viewModel.pauseTracking(requireContext())
        }

        binding.fabToSessionStatistics.setOnClickListener {
            it.visibility = View.GONE
            binding.fabToSessionMap.visibility = View.VISIBLE
            childFragmentNavController.navigate(R.id.action_trackerFragment3_to_trackerStatisticsFragment4)
        }

        binding.fabToSessionMap.setOnClickListener {
            it.visibility = View.GONE
            binding.fabToSessionStatistics.visibility = View.VISIBLE
            childFragmentNavController.navigate(R.id.action_trackerStatisticsFragment4_to_trackerFragment3)
        }

        binding.fabResumeTracking.setOnClickListener {
            viewModel.resumeTracking(requireContext())
        }

        binding.fabFinishTracking.setOnClickListener {
            viewModel.stopService(requireContext())
            findNavController().navigate(R.id.action_sessionRecordingFragment_to_mainFragment)
        }

        viewModel.currentFragmentIdLiveData.observe(viewLifecycleOwner) { currentFragmentId ->
            when (currentFragmentId) {
                R.id.trackerFragment3 -> {
                    if (TrackService.serviceLifecycleState.value != ServiceLifecycleState.NOT_STARTED) {
                        binding.fabToSessionStatistics.visibility = View.VISIBLE
                        binding.fabToSessionMap.visibility = View.GONE
                    }
                }
                R.id.trackerStatisticsFragment4 -> {
                    if (TrackService.serviceLifecycleState.value != ServiceLifecycleState.NOT_STARTED) {
                        binding.fabToSessionMap.visibility = View.VISIBLE
                        binding.fabToSessionStatistics.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSessionRecordingBinding =
        FragmentSessionRecordingBinding.inflate(inflater, container, false)
}