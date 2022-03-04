package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.EventsRepository
import javax.inject.Inject

class GetLastEventUseCase @Inject constructor(
    private val repository: EventsRepository
) {
    suspend fun execute() = repository.getLastEvent()
}