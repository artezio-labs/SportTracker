package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.EventsRepository
import javax.inject.Inject

class GetTrackingStateByEventId @Inject constructor(
    private val repository: EventsRepository
) {
    suspend fun execute(eventId: Long) = repository.getTrackingStateByEventId(eventId)
}