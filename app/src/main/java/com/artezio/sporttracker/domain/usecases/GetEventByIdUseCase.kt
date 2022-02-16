package com.artezio.sporttracker.domain.usecases

import com.artezio.sporttracker.data.repository.EventsRepository
import javax.inject.Inject

class GetEventByIdUseCase @Inject constructor(
    private val repository: EventsRepository
) {

    suspend fun execute(id: Long) = repository.getEventById(id)

}