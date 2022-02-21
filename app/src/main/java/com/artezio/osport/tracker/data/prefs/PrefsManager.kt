package com.artezio.osport.tracker.data.prefs

import android.content.Context
import android.util.Log

class PrefsManager(context: Context) {
    private val prefs = context.getSharedPreferences("some_states", Context.MODE_PRIVATE)
    var state: Boolean
        get() {
            val state = prefs.getBoolean("state", false)
            Log.d("steps", "State: $state")
            return state
        }
        set(value) {
            prefs.edit().apply {
                putBoolean("state", value)
            }.apply()
        }
}