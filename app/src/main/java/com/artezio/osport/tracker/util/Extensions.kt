package com.artezio.osport.tracker.util

import android.location.Location
import com.artezio.osport.tracker.domain.model.LocationPointData
import com.artezio.osport.tracker.domain.model.TrackingStateModel

fun LocationPointData.toLocation() = Location("").apply {
    latitude = latitude
    longitude = longitude
}

fun distanceBetween(firstLocation: LocationPointData, secondLocation: LocationPointData): Float {
    val firstPoint = Location("fp").apply {
        longitude = firstLocation.longitude
        latitude = firstLocation.latitude
    }
    val secondPoint = Location("sp").apply {
        longitude = secondLocation.longitude
        latitude =  secondLocation.latitude
    }
    return firstPoint.distanceTo(secondPoint)
}

fun TrackingStateModel.isNotEmpty() = this != TrackingStateModel.empty()