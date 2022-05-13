package com.artezio.osport.tracker.util

import android.location.Location
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.artezio.osport.tracker.domain.model.LocationPointData

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

fun <T> LiveData<T>.observeNonNull(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner) {
        it?.let(observer)
    }
}