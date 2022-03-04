package com.artezio.osport.tracker.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.artezio.osport.tracker.domain.model.TrackingStateModel
import kotlinx.coroutines.flow.first

class DataStore(val context: Context) {

    private val Context.datastore by preferencesDataStore(name = DATA_STORE_NAME)

    suspend fun saveTrackingState(state: TrackingStateModel) {
        context.datastore.edit { prefs ->
            prefs[TIMER_VALUE] = state.timerValue
            prefs[SPEED_VALUE] = state.speedValue
            prefs[DISTANCE_VALUE] = state.distanceValue
            prefs[TEMPO_VALUE] = state.tempoValue
            prefs[STEPS_VALUE] = state.stepsValue
            prefs[GPS_POINTS_VALUE] = state.gpsPointsValue
        }
    }

    suspend fun getTrackingState(): TrackingStateModel {
        val prefs = context.datastore.data.first()
        return TrackingStateModel(
            timerValue = prefs[TIMER_VALUE] ?: 0.0,
            speedValue = prefs[SPEED_VALUE] ?: 0.0,
            distanceValue = prefs[DISTANCE_VALUE] ?: 0.0,
            tempoValue = prefs[TEMPO_VALUE] ?: 0.0,
            stepsValue = prefs[STEPS_VALUE] ?: 0,
            gpsPointsValue = prefs[GPS_POINTS_VALUE] ?: 0
        )
    }



    companion object {
        private const val DATA_STORE_NAME = "tracker_state_datastore"

        private val TIMER_VALUE = doublePreferencesKey("timer_value")
        private val SPEED_VALUE = doublePreferencesKey("speed_value")
        private val DISTANCE_VALUE = doublePreferencesKey("distance_value")
        private val STEPS_VALUE = intPreferencesKey("steps_value")
        private val GPS_POINTS_VALUE = intPreferencesKey("steps_value")
        private val TEMPO_VALUE = doublePreferencesKey("tempo_value")
    }
}