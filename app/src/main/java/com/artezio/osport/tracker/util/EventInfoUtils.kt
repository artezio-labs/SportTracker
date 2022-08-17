package com.artezio.osport.tracker.util

import android.util.Log
import com.artezio.osport.tracker.domain.model.LocationPointData
import com.artezio.osport.tracker.domain.model.PedometerData

object EventInfoUtils {

    fun calculateAvgCadence(data: List<PedometerData>): Int {
        if (data.size <= 1) return 0
        val time = data.last().time - data.first().time
        val dataPerLastMinute = data.filter { data.last().time - it.time <= MINUTE_IN_MILLIS }
        return if (time < MINUTE_IN_MILLIS) {
            val minute = time.toDouble() / MINUTE_IN_MILLIS
            (dataPerLastMinute.last().stepCount / minute).toInt()
        } else {
            val stepCount = data.last().stepCount
            val dataPerMinutes = data.groupDataByMinutes()
            Log.d("cadence", "Data by minutes: $dataPerMinutes")
            val lastMinuteData = dataPerMinutes.values.last()
            return if (lastMinuteData.size <= 1) {
                0
            } else {
                calculateDataPerMinute(lastMinuteData)
            }
        }
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
        return data.map { it.speed }.average()
    }

    private fun calculateDataPerMinute(data: List<PedometerData>): Int {
        val timeDiff = (data.last().time - data.first().time).toDouble() / MINUTE_IN_MILLIS
        val stepPerMinute = data.last().stepCount - data.first().stepCount
        Log.d(
            "cadence_calc",
            "calculateAvgCadence: stepPerMinute = $stepPerMinute, timeDiff = $timeDiff"
        )
        return (stepPerMinute / timeDiff).toInt()
    }
}