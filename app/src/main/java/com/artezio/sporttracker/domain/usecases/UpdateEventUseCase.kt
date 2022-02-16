package com.artezio.sporttracker.domain.usecases

import com.artezio.sporttracker.data.repository.EventsRepository
import com.artezio.sporttracker.domain.model.Event
import javax.inject.Inject

class UpdateEventUseCase @Inject constructor(
    private val repository: EventsRepository
) {

    suspend fun execute(id: Long, name: String, startDate: Long) = repository.updateEvent(id, name, startDate)

}