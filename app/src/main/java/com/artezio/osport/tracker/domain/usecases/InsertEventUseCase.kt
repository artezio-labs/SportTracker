package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.EventsRepository
import com.artezio.osport.tracker.domain.model.Event
import javax.inject.Inject

class InsertEventUseCase @Inject constructor(
    private val repository: EventsRepository
) {

    suspend fun execute(event: Event) = repository.addEvent(event)

}