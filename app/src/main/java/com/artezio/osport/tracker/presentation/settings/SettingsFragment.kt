package com.artezio.osport.tracker.presentation.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.preferences.SettingsPreferencesManager
import com.artezio.osport.tracker.databinding.FragmentSettingsBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.util.DialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding, SettingsViewModel>() {

    override val viewModel: SettingsViewModel by viewModels()

    override var bottomNavigationViewVisibility: Int = View.GONE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeValues()

        binding.linearLayoutFrequencySetting.setOnClickListener {
            showGpsSettingBottomNavigationDialog(true)
        }

        binding.linearLayoutDistanceSetting.setOnClickListener {
            showGpsSettingBottomNavigationDialog(false)
        }

        binding.buttonClose.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_profileFragment)
        }

        binding.textViewAboutApp.setOnClickListener {
            showAboutAppDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("track_settings", "onResume: ")
    }

    private fun observeValues() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getSettingValue(true).collect {
                binding.settingFrequencyValue.text = viewModel.getSettingValueString(true, it)
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getSettingValue(false).collect {
                binding.settingDistanceValue.text = viewModel.getSettingValueString(false, it)
            }
        }
    }

    private fun showGpsSettingBottomNavigationDialog(flag: Boolean) {
        val dialog = GpsSettingInputBottomSheetDialog().apply {
            arguments = bundleOf("gps_setting" to flag)
        }
        dialog.show(childFragmentManager, GpsSettingInputBottomSheetDialog.TAG)
    }

    private fun showAboutAppDialog() {
        DialogBuilder(
            context = requireContext(),
            title = getString(R.string.dialog_about_app_title_text),
            message = getAppVersionInfo(),
            positiveButtonText = getString(R.string.button_close_text),
            positiveButtonClick = { dialog, _ -> dialog.cancel() },
            needsToShow = true
        ).build()
    }

    private fun getAppVersionInfo(): String {
        val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
        val versionName = packageInfo.versionName
        val versionCode = packageInfo.longVersionCode.toInt()
        return "Версия $versionName ($versionCode)\n\n${viewModel.getBuildInfoFromAssets()}"
    }


    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }
}