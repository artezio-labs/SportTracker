package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.EventsRepository
import javax.inject.Inject

class DeletePlannedEventUseCase @Inject constructor(
    private val eventsRepository: EventsRepository
) {
    suspend fun execute(id: Long) = eventsRepository.deletePlannedEventById(id)
}
