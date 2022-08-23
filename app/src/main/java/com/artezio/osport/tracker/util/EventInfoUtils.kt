package com.artezio.osport.tracker.util

import android.util.Log
import com.artezio.osport.tracker.domain.model.LocationPointData
import com.artezio.osport.tracker.domain.model.PedometerData

object EventInfoUtils {

    fun calculateCadence(data: List<PedometerData>): Int {
        if (data.size <= 1) return 0
        val time = data.last().time - data.first().time
        val timeInMinutes = time.toDouble() / MINUTE_IN_MILLIS
        return (data.last().stepCount / timeInMinutes).toInt()
    }

    fun calculateDistance(locations: List<LocationPointData>): Double {
        var totalDistance = 0.0
        if (locations.size <= 1) return totalDistance
        for (i in 0 until locations.size - 1) {
            totalDistance += distanceBetween(locations[i], locations[i + 1])
            Log.d("event_info", "Distance between: $totalDistance")
        }
        return totalDistance
    }

    fun filterData(id: Long, data: List<PedometerData>): List<PedometerData> {
        if (data.size <= 1) return data
        val filteredData = mutableListOf<PedometerData>().apply {
            add(data[0])
        }
        for (i in 1 until data.size - 1) {
            if (data[i].time - data[i - 1].time <= CADENCE_STEP_FILTER_VALUE) {
                filteredData.add(data[i])
            }
        }
        return filteredData.filter { it.eventId == id }
    }

    fun calculateAvgSpeed(data: List<LocationPointData>): Double {
        if (data.isEmpty()) return 0.0
        val distance = calculateDistance(data)
        val time = (data.last().time - data.first().time).toDouble() / HOUR_IN_MILLIS
        return (distance / 1000) / time
    }
}