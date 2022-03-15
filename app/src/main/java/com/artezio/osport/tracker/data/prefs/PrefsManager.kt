package com.artezio.osport.tracker.data.prefs

import android.content.Context
import android.util.Log
import com.artezio.osport.tracker.domain.model.TrackingStateModel
import com.google.gson.Gson

class PrefsManager(context: Context) {
    private val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    var trackingState: TrackingStateModel
        get() {
            val trackingState = if (prefs.contains(TRACKING_STATE_VALUE)) prefs.getString(
                TRACKING_STATE_VALUE,
                ""
            ) else ""
            return if (!trackingState.isNullOrEmpty()) gson.fromJson(
                trackingState,
                TrackingStateModel::class.java
            )
            else TrackingStateModel.empty()
        }
        set(value) {
            val editor = prefs.edit()
            editor.apply {
                Log.d("event_save", "Value: $value")
                putString(TRACKING_STATE_VALUE, gson.toJson(value))
            }.apply()
        }

    var steps: Int
        get() = prefs.getInt(STEPS_VALUE, 0)
        set(value) {
            prefs.edit().apply {
                putInt(STEPS_VALUE, value)
            }.apply()
        }

    fun clearPrefs() {
        prefs.edit().clear().apply()
    }

    fun clearStepsValue() {
        prefs.edit().remove(STEPS_VALUE).apply()
    }

    companion object {
        private const val TRACKING_STATE_VALUE = "tracking_value"
        private const val STEPS_VALUE = "steps_value"
        private const val PREFERENCES_NAME = "tracking_preferences"
    }
}