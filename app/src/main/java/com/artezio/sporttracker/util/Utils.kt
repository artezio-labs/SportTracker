package com.artezio.sporttracker.util

import android.Manifest
import android.content.Context
import android.os.Build
import com.artezio.sporttracker.domain.model.Event
import com.artezio.sporttracker.domain.model.EventWithData
import com.artezio.sporttracker.presentation.main.recycler.Item
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*

fun millisecondsToDateFormat(milliseconds: Long): String =
    SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date(milliseconds))

fun dateToMilliseconds(date: String): Long =
    SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).parse(date).time

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

fun mapDomainEventModelToPresentationEventModel(eventDomain: EventWithData) =
    Item.Event(
        id = eventDomain.event.id,
        eventName = eventDomain.event.name,
        startDate = eventDomain.event.startDate,
        endDate = eventDomain.event.endDate
    )


const val START_FOREGROUND_SERVICE = "START_FOREGROUND"
const val STOP_FOREGROUND_SERVICE = "STOP_FOREGROUND"