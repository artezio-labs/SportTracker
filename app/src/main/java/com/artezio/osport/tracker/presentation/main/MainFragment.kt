package com.artezio.osport.tracker.presentation.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.artezio.osport.tracker.databinding.FragmentMainBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.presentation.main.recycler.EventsRecyclerAdapter
import com.artezio.osport.tracker.presentation.main.viewpager.ViewPagerAdapter
import com.artezio.osport.tracker.presentation.tracker.ScheduleTrackingBottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>(), IFragment {

    override var onBackPressed: Boolean = false
    override val viewModel: MainViewModel by viewModels()

    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewpager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.trackerTabs, binding.viewpager) { tab, position ->
            tab.text = viewModel.getTabsTitles()[position]
        }.attach()
        binding.buttonPlanTrack.setOnClickListener {
            val bottomSheet = ScheduleTrackingBottomSheetDialog()
            bottomSheet.show(
                childFragmentManager,
                ScheduleTrackingBottomSheetDialog.TAG
            )
        }
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMainBinding =
        FragmentMainBinding.inflate(inflater, container, false)
}
