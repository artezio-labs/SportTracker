package com.artezio.osport.tracker.util

import android.Manifest
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.fragment.app.Fragment
import com.artezio.osport.tracker.R
import pub.devrel.easypermissions.EasyPermissions

private const val PERMISSIONS_REQUEST_CODE = 9465

fun hasLocationAndActivityRecordingPermission(context: Context) =
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    } else {
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    }

fun hasForegroundPermissions(context: Context) =
    EasyPermissions.hasPermissions(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

fun hasBackgroundLocationPermission(context: Context): Boolean {
    val hasPermission = EasyPermissions.hasPermissions(
        context,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    )
    Log.d("permissions_state", "${Manifest.permission.ACCESS_BACKGROUND_LOCATION} is granted = $hasPermission!")
    return hasPermission
}




