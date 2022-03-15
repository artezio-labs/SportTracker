package com.artezio.osport.tracker.data.permissions

interface PermissionsManager {
    fun hasPermissionsGranted(): Boolean
    fun requestLocationPermissions()
}