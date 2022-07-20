package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.EventsRepository
import com.artezio.osport.tracker.domain.model.PlannedEvent
import javax.inject.Inject

open class UpdateEventUseCase @Inject constructor(
    private val repository: EventsRepository
) {
    suspend fun execute(id: Long, name: String, startDate: Long) =
        repository.updateEvent(id, name, startDate)

    suspend fun execute(startDate: Long, name: String, time: Double) =
        repository.updateEvent(startDate, name, time)

    suspend fun execute(id: Long, event: PlannedEvent) =
        repository.updatePlannedEvent(id, event)
}