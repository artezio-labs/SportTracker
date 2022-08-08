package com.artezio.osport.tracker.presentation.settings

import androidx.lifecycle.MutableLiveData
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.preferences.SettingsPreferencesManager
import com.artezio.osport.tracker.presentation.BaseViewModel
import com.artezio.osport.tracker.util.ResourceProvider
import com.artezio.osport.tracker.util.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsPreferencesManager: SettingsPreferencesManager,
    private val resourceProvider: ResourceProvider
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
}