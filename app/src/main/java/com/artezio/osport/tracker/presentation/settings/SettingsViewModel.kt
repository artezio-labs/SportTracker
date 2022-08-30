package com.artezio.osport.tracker.presentation.settings

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.preferences.SettingsPreferencesManager
import com.artezio.osport.tracker.domain.model.BuildInfo
import com.artezio.osport.tracker.presentation.BaseViewModel
import com.artezio.osport.tracker.util.AssetsProvider
import com.artezio.osport.tracker.util.BUILD_INFO_FILE_NAME
import com.artezio.osport.tracker.util.ResourceProvider
import com.artezio.osport.tracker.util.ValidationUtils
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsPreferencesManager: SettingsPreferencesManager,
    private val resourceProvider: ResourceProvider,
    private val assetsProvider: AssetsProvider,
) : BaseViewModel() {

    fun setTitle(flag: Boolean): String {
        return if (flag) {
            resourceProvider.getString(R.string.gps_setting_frequency_input_title_text)
        } else {
            resourceProvider.getString(R.string.gps_setting_distance_input_title_text)
        }
    }

    fun setHint(flag: Boolean): String {
        return if (flag) {
            resourceProvider.getString(R.string.gps_setting_frequency_input_hint_text)
        } else {
            resourceProvider.getString(R.string.gps_setting_distance_input_hint_text)
        }
    }

    suspend fun saveSetting(settingFlag: Boolean, value: Int) {
        if (settingFlag) {
            settingsPreferencesManager.save(SettingsPreferencesManager.GPS_FREQUENCY_KEY, value)
        } else {
            settingsPreferencesManager.save(SettingsPreferencesManager.GPS_DISTANCE_KEY, value)

        }
    }

    suspend fun getSettingValue(settingFlag: Boolean): Flow<Int> {
        return settingsPreferencesManager.get(settingFlag)
    }

    fun getSettingValueString(settingFlag: Boolean, value: Int): String {
        return if (settingFlag) {
            "$value сек."
        } else {
            "$value м."
        }
    }

    fun getBuildInfoFromAssets(): String {
        val buildInfoString = assetsProvider.readJsonFileFromAssets(BUILD_INFO_FILE_NAME)
        Log.d("build_info", "getBuildInfoFromAssetsString: $buildInfoString")
        val buildInfo = if (!buildInfoString.isNullOrEmpty()) {
            Gson().fromJson(buildInfoString, BuildInfo::class.java)
        } else {
            BuildInfo()
        }
        return "Сборка ${buildInfo.versionBuildName} (${buildInfo.versionBuildId})"
    }
}