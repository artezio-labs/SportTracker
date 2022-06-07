package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.EventsRepository
import com.artezio.osport.tracker.data.repository.LocationRepository
import com.artezio.osport.tracker.data.repository.PedometerRepository
import com.artezio.osport.tracker.domain.model.EventInfo
import com.artezio.osport.tracker.util.EventInfoUtils.calculateAvgCadence
import com.artezio.osport.tracker.util.EventInfoUtils.calculateAvgSpeed
import com.artezio.osport.tracker.util.EventInfoUtils.calculateDistance
import com.artezio.osport.tracker.util.EventInfoUtils.filterData
import com.artezio.osport.tracker.util.ResourceProvider
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
        val pedometerData = pedometerRepository.getAllPedometerData()
        val wholePedometerData = filterData(id, pedometerData)
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
}