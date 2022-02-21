package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.EventsRepository
import javax.inject.Inject

class GetEventByIdUseCase @Inject constructor(
    private val repository: EventsRepository
) {

    suspend fun execute(id: Long) = repository.getEventById(id)

}