package com.artezio.osport.tracker.data.permissions

import androidx.lifecycle.LiveData

interface ISystemServicePermissionsManager {
    fun hasNotificationPermissionEnabled(): Boolean
    fun hasPowerSafeModePermissionEnabled(): Boolean
    fun sendUserToPowerSettings()
    fun sendUserToAppNotificationSettings()
    fun hasGPSEnabled(): LiveData<Boolean>
}