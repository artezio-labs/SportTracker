package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.EventsRepository
import javax.inject.Inject

class GetAllEventsListUseCase @Inject constructor(
    private val repository: EventsRepository
) {
    suspend fun execute() = repository.getAllEventsList()
        .sortedByDescending { it.startDate }
        .map { it.name }
}