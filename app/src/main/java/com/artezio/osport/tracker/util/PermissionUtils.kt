package com.artezio.osport.tracker.util

import android.Manifest
import android.content.Context
import android.os.Build
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

fun requestLocationPermissions(fragment: Fragment) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        EasyPermissions.requestPermissions(
            fragment,
            fragment.getString(R.string.location_permissions_need_to_be_granted_dialog_message),
            PERMISSIONS_REQUEST_CODE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    } else {
        EasyPermissions.requestPermissions(
            fragment,
            fragment.getString(R.string.location_permissions_need_to_be_granted_dialog_message),
            PERMISSIONS_REQUEST_CODE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    }
}

fun requestPhysicalActivityPermissions(fragment: Fragment) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        EasyPermissions.requestPermissions(
            fragment,
            fragment.getString(R.string.activity_permissions_need_to_be_granted_dialog_message),
            PERMISSIONS_REQUEST_CODE,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    }
}

fun hasPermission(context: Context, vararg permissions: String): Boolean =
    EasyPermissions.hasPermissions(context, *permissions)




