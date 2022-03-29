package com.artezio.osport.tracker.data.permissions

interface IPermissionsManager {
    fun hasLocationPermissionsGranted(): Boolean
    fun requestMultiplePermissions()
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean
}