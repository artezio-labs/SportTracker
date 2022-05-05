package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.EventsRepository
import javax.inject.Inject

class UpdateEventNameUseCase @Inject constructor(
    private val repository: EventsRepository
) {
    suspend fun execute(name: String, startDate: Long) = repository.updateEventName(name, startDate)
}