package com.artezio.osport.tracker.data.permissions.chain

import com.artezio.osport.tracker.data.permissions.PermissionsManager
import com.artezio.osport.tracker.presentation.MainActivity

class LocationPermissionLink(
    activity: MainActivity
) : Link() {

    private val permissionsManager = PermissionsManager(activity)

    override fun check(): Boolean {
        return if (!permissionsManager.hasLocationPermissionsGranted()) {
            permissionsManager.request()
            false
        } else {
            super.checkNext()
            true
        }
    }

}