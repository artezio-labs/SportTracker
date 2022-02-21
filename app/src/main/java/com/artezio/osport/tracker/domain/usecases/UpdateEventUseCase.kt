package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.EventsRepository
import javax.inject.Inject

class UpdateEventUseCase @Inject constructor(
    private val repository: EventsRepository
) {

    suspend fun execute(id: Long, name: String, startDate: Long) = repository.updateEvent(id, name, startDate)

}