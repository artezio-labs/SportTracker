package com.artezio.osport.tracker.data.permissions.chain

import android.content.Context
import com.artezio.osport.tracker.data.permissions.SystemServicePermissionsManager

class NotificationPermissionLink(
    context: Context
) : Link() {

    private val systemServicePermissionsManager = SystemServicePermissionsManager(context)

    override fun check(): Boolean {
        return if (!systemServicePermissionsManager.hasNotificationPermissionEnabled()) {
            systemServicePermissionsManager.sendUserToAppNotificationSettings()
            false
        } else {
            super.checkNext()
            true
        }
    }
}