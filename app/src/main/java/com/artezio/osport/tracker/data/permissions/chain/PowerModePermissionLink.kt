package com.artezio.osport.tracker.data.permissions.chain

import android.content.Context
import androidx.navigation.NavController
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.permissions.SystemServicePermissionsManager

class PowerModePermissionLink(
    context: Context,
    private val navController: NavController
) : Link() {

    private val systemServicePermissionsManager = SystemServicePermissionsManager(context)

    override fun check(): Boolean {
        return if (!systemServicePermissionsManager.hasPowerSafeModePermissionEnabled()) {
            systemServicePermissionsManager.sendUserToPowerSettings()
            false
        } else {
            navController.navigate(R.id.action_mainFragment_to_sessionRecordingFragment)
            true
        }
    }
}