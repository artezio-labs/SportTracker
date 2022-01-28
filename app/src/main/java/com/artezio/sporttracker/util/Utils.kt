package com.artezio.sporttracker.util

import android.Manifest
import android.content.Context
import android.os.Build
import com.artezio.sporttracker.domain.model.Event
import com.artezio.sporttracker.presentation.main.recycler.Item
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

fun mapDomainEventModelToPresentationEventModel(eventDomain: Event) =
    Item.Event(
        eventName = eventDomain.name,
        startDate = eventDomain.startDate,
        endDate = eventDomain.endDate ?: 0L
    )

const val START_FOREGROUND_SERVICE = "START_FOREGROUND"
const val STOP_FOREGROUND_SERVICE = "STOP_FOREGROUND"