package com.artezio.sporttracker.domain.usecases

import com.artezio.sporttracker.data.repository.EventsRepository
import com.artezio.sporttracker.domain.model.Event
import javax.inject.Inject

class InsertEventUseCase @Inject constructor(
    private val repository: EventsRepository
) {

    suspend fun execute(event: Event) = repository.addEvent(event)

}