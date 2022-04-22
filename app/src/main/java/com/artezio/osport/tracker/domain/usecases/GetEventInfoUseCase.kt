package com.artezio.osport.tracker.domain.usecases

import android.util.Log
import com.artezio.osport.tracker.data.repository.EventsRepository
import com.artezio.osport.tracker.data.repository.LocationRepository
import com.artezio.osport.tracker.data.repository.PedometerRepository
import com.artezio.osport.tracker.domain.model.EventInfo
import com.artezio.osport.tracker.domain.model.LocationPointData
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
        val title = event.name
        val time = event.timerValue
        val speed = event.speedValue
        val distance = calculateDistance(locations) / 1000
        val tempo = ((time / 60) + (time % 60)) / distance
        return EventInfo(
            title = title,
            time = formatTime(event.timerValue, resourceProvider),
            speed = String.format("%.2f км/ч", speed),
            distance = String.format("%.2f км", calculateDistance(locations) / 1000),
            tempo = String.format("%.2f мин/км", tempo),
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
}