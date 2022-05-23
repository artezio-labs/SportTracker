package com.artezio.osport.tracker.domain.usecases

import android.util.Log
import com.artezio.osport.tracker.data.repository.EventsRepository
import com.artezio.osport.tracker.data.repository.LocationRepository
import com.artezio.osport.tracker.data.repository.PedometerRepository
import com.artezio.osport.tracker.domain.model.EventInfo
import com.artezio.osport.tracker.domain.model.LocationPointData
import com.artezio.osport.tracker.domain.model.PedometerData
import com.artezio.osport.tracker.util.ResourceProvider
import com.artezio.osport.tracker.util.distanceBetween
import com.artezio.osport.tracker.util.formatTime
import javax.inject.Inject

class GetEventInfoUseCase @Inject constructor(
    private val eventsRepository: EventsRepository,
    private val locationsRepository: LocationRepository,
    private val pedometerRepository: PedometerRepository,
    private val resourceProvider: ResourceProvider
) {
    suspend fun execute(id: Long): EventInfo {
        val event = eventsRepository.getEventById(id)
        val locations = locationsRepository.getAllLocationsById(id)
        val steps = pedometerRepository.getStepCount(id)
        val wholePedometerData = filterData(id)
        val title = event.name
        val time = event.timerValue
        val speed = event.speedValue
        val distance = calculateDistance(locations) / 1000
        val tempo = ((time / 60) + (time % 60)) / distance
        val cadence = prepareCadenceData(wholePedometerData)
        return EventInfo(
            title = title,
            time = formatTime(event.timerValue, resourceProvider),
            speed = String.format("%.2f км/ч", speed),
            distance = String.format("%.2f км", calculateDistance(locations) / 1000),
            tempo = String.format("%.2f мин/км", tempo),
            cadence = cadence.toString(),
            steps = if (steps == null) "0" else steps.stepCount.toString(),
            gpsPoints = event.gpsPointsValue.toString()
        )
    }

    private fun calculateDistance(locations: List<LocationPointData>): Double {
        var totalDistance = 0.0
        if (locations.size <= 1) return totalDistance
        for (i in 0 until locations.size - 1) {
            totalDistance += distanceBetween(locations[i], locations[i + 1])
            Log.d("event_info", "Distance between: $totalDistance")
        }
        return totalDistance
    }

    private fun prepareCadenceData(data: List<PedometerData>): Int {
        val groupedData = groupDataByMinutes(data)
        logCadenceData(groupedData)
        val cadences = mutableListOf<Int>()
        if (groupedData.isNotEmpty()) {
            for ((minutes, dataPerMinute) in groupedData) {
                cadences.add(calculateCadence(minutes, dataPerMinute))
            }
        }
        Log.d("CADENCES", "Cadences list: $cadences")
        return cadences.average().toInt()
    }

    private fun calculateCadence(minutes: Int, data: List<PedometerData>): Int {
        return (data.last().stepCount - data.first().stepCount) / minutes
    }

    private suspend fun filterData(id: Long): List<PedometerData> {
        val data = pedometerRepository.getAllPedometerData()
        if (data.size <= 1) return data
        val filteredData = mutableListOf<PedometerData>().apply {
            add(data[0])
        }
        for (i in 1 until data.size - 1) {
            if (data[i].time - data[i - 1].time <= 3_000) {
                filteredData.add(data[i])
            }
        }
        return filteredData.filter { it.eventId == id }
    }

    private fun groupDataByMinutes(data: List<PedometerData>): Map<Int, List<PedometerData>> {
        if (data.isEmpty()) return mutableMapOf()
        val trackStartTime = data.first().time
        val minutes = mutableListOf<Int>()
        data.forEachIndexed { i, _ ->
            val minute = (data[i].time - trackStartTime).toInt() / 60_000
            minutes.add(if (minute == 0) 1 else minute)
        }
        return minutes.zip(data).groupBy { it.first }
            .mapValues { values -> values.value.map { it.second } }
    }

    private fun logCadenceData(data: Map<Int, List<PedometerData>>) {
        for ((k, v) in data) {
            Log.d("CADENCE", "Minute: $k, data: $v")
        }
    }
}