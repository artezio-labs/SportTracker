package com.artezio.osport.tracker.presentation.main

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.permissions.PermissionsManager
import com.artezio.osport.tracker.data.permissions.SystemServicePermissionsManager
import com.artezio.osport.tracker.databinding.FragmentMainBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.presentation.MainActivity
import com.artezio.osport.tracker.presentation.main.viewpager.ViewPagerAdapter
import com.artezio.osport.tracker.presentation.tracker.ScheduleTrackingBottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>(), IFragment {

    private val permissionsManager: PermissionsManager by lazy {
        PermissionsManager(activity as MainActivity)
    }
    private val systemServicePermissionsManager by lazy {
        SystemServicePermissionsManager(requireContext())
    }

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
            val isNotificationsEnabled =
                systemServicePermissionsManager.hasNotificationPermissionEnabled()
            val isPowerSafeModeEnabled =
                systemServicePermissionsManager.hasPowerSafeModePermissionEnabled()
            Log.d("permissions_states", "$isNotificationsEnabled $isPowerSafeModeEnabled")
            Log.d("permissions_states", "Manufacturer: ${Build.MANUFACTURER}")
            if (permissionsManager.hasLocationPermissionsGranted()) {
                if (systemServicePermissionsManager.hasNotificationPermissionEnabled()) {
                    if (!systemServicePermissionsManager.hasPowerSafeModePermissionEnabled()) {
                        Log.d("permissions_state", "All permissions is granted")
                        val bottomSheet = ScheduleTrackingBottomSheetDialog()
                        bottomSheet.show(
                            childFragmentManager,
                            ScheduleTrackingBottomSheetDialog.TAG
                        )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.warning_turn_off_doze_mode),
                            Toast.LENGTH_SHORT
                        ).show()
                        systemServicePermissionsManager.sendUserToPowerSettings()
                    }
                } else {
                    systemServicePermissionsManager.sendUserToAppNotificationSettings()
                }
            } else {
                permissionsManager.request()
            }

        }
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMainBinding =
        FragmentMainBinding.inflate(inflater, container, false)
}
