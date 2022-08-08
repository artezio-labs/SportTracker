package com.artezio.osport.tracker.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.liveData
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class SettingsPreferencesManager @Inject constructor(
    private val context: Context
) : PreferencesManager<Int> {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override suspend fun save(tag: String, value: Int) {
        context.dataStore.edit { settings ->
            settings[intPreferencesKey(tag)] = value
        }
    }

    override suspend fun get(flag: Boolean): Flow<Int> {
        return context.dataStore.data.map { settings ->
            when (flag) {
                true -> {
                    settings[intPreferencesKey(GPS_FREQUENCY_KEY)] ?: 1
                }
                false -> {
                    settings[intPreferencesKey(GPS_DISTANCE_KEY)] ?: 100
                }
            }
        }
    }

    companion object {
        const val GPS_FREQUENCY_KEY = "gps_frequency"
        const val GPS_DISTANCE_KEY = "gps_distance"
    }
}