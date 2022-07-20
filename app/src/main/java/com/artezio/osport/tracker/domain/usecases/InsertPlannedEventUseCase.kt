package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.EventsRepository
import com.artezio.osport.tracker.domain.model.PlannedEvent
import javax.inject.Inject

class InsertPlannedEventUseCase @Inject constructor(
    private val repository: EventsRepository
) {
    suspend fun execute(event: PlannedEvent) = repository.insertPlannedEvent(event)
}