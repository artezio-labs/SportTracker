package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.EventsRepository
import com.artezio.osport.tracker.domain.model.Event
import com.artezio.osport.tracker.domain.model.TrackingStateModel
import javax.inject.Inject

open class UpdateEventUseCase @Inject constructor(
    private val repository: EventsRepository
) {
    suspend fun execute(id: Long, name: String, startDate: Long) =
        repository.updateEvent(id, name, startDate)

    suspend fun execute(startDate: Long, trackingStateModel: TrackingStateModel) =
        repository.updateEvent(startDate, trackingStateModel)

    suspend fun execute(startDate: Long, name: String, trackingStateModel: TrackingStateModel) =
        repository.updateEvent(startDate, name, trackingStateModel)

    suspend fun execute(event: Event) = repository.updateEvent(event)
}