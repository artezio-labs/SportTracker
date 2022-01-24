package com.artezio.sporttracker.util

import android.Manifest
import android.content.Context
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*

fun millisecondsToDateFormat(milliseconds: Long): String =
    SimpleDateFormat("yyyy-MM-dd'HH:mm:ss", Locale.getDefault()).format(Date(milliseconds))

fun hasLocationPermission(context: Context) =
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    } else {
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    }
