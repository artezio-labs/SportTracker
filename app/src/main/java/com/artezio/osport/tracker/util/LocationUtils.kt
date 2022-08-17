package com.artezio.osport.tracker.util

import android.location.Location
import com.artezio.osport.tracker.data.mappers.IMapper
import com.artezio.osport.tracker.domain.model.LocationPointData

fun distanceToString(distance: Double): String {
    return String.format("%.2f км", distance / 1000)
}

fun distanceBetween(firstLocation: LocationPointData, secondLocation: LocationPointData): Float {
    val firstPoint = Location("fp").apply {
        longitude = firstLocation.longitude
        latitude = firstLocation.latitude
    }
    val secondPoint = Location("sp").apply {
        longitude = secondLocation.longitude
        latitude = secondLocation.latitude
    }
    return firstPoint.distanceTo(secondPoint)
}

fun calculateRouteDistance(locations: List<LocationPointData>): Double {
    if (locations.isEmpty()) return 0.0
    var distance = 0.0
    for (i in 0 until locations.size - 1) {
        distance += distanceBetween(locations[i], locations[i + 1])
    }
    return distance
}

fun isLocationPassedFilter(
    frequencyValue: Long,
    distanceValue: Int,
    currentLocation: Location,
    prevLocation: Location,
    mapper: IMapper<Location, LocationPointData>
): Boolean {
    if (currentLocation == prevLocation) return false
    val distance = distanceBetween(mapper.map(currentLocation), mapper.map(prevLocation))
    val timeBetween = currentLocation.time - prevLocation.time
    return distance <= distanceValue && timeBetween <= frequencyValue
}