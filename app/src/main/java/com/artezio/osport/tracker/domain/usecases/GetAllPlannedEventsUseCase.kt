package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.EventsRepository
import javax.inject.Inject

class GetAllPlannedEventsUseCase @Inject constructor(
    private val eventsRepository: EventsRepository
) {
    fun executeWithFlow() = eventsRepository.getPlannedEvents()
    suspend fun execute() = eventsRepository.getPlannedEventsList()
}