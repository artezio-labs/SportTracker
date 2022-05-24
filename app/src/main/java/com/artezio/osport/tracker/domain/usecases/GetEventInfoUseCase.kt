package com.artezio.osport.tracker.domain.usecases

import android.util.Log
import com.artezio.osport.tracker.data.repository.EventsRepository
import com.artezio.osport.tracker.data.repository.LocationRepository
import com.artezio.osport.tracker.data.repository.PedometerRepository
import com.artezio.osport.tracker.domain.model.EventInfo
import com.artezio.osport.tracker.domain.model.LocationPointData
import com.artezio.osport.tracker.domain.model.PedometerData
import com.artezio.osport.tracker.util.*
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
        val speed = calculateAvgSpeed(locations)
        val distance = calculateDistance(locations) / 1000
        val tempo = ((time / 60) + (time % 60)) / distance
        val cadence = calculateAvgCadence(wholePedometerData)
        return EventInfo(
            title = title,
            time = formatTime(event.timerValue, resourceProvider),
            speed = String.format("%.2f км/ч", speed),
            distance = String.format("%.2f км", calculateDistance(locations) / 1000),
            tempo = String.format("%.2f мин/км", tempo),
            cadence = cadence.toString(),
            steps = if (steps == null) "0" else steps.stepCount.toString(),
            gpsPoints = locations.size.toString()
        )
    }

    private fun calculateAvgCadence(data: List<PedometerData>): Int {
        if (data.isEmpty()) return 0
        if (data.size == 1) return 1
        val cadences = mutableListOf<Int>()
        for (i in 1 until data.size - 1) {
            val sublist = data.subList(0, i)
            val currentMinuteData = sublist.filter { (sublist.last().time - it.time) <= MINUTE }
            cadences.add(currentMinuteData.last().stepCount - currentMinuteData.first().stepCount)
        }
        return cadences.average().toInt()
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

    private suspend fun filterData(id: Long): List<PedometerData> {
        val data = pedometerRepository.getAllPedometerData()
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

    private fun calculateAvgSpeed(data: List<LocationPointData>): Double {
        if (data.isEmpty()) return 0.0
        return data.map { it.speed }.average()
    }
}