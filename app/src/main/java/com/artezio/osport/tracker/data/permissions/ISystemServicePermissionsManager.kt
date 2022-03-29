package com.artezio.osport.tracker.data.permissions

interface ISystemServicePermissionsManager {
    fun hasNotificationPermissionEnabled(): Boolean
    fun hasPowerSafeModePermissionEnabled(): Boolean
    fun sendUserToPowerSettings()
    fun sendUserToAppNotificationSettings()
}