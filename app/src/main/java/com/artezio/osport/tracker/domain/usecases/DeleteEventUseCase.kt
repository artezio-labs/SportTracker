package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.EventsRepository
import javax.inject.Inject

open class DeleteEventUseCase @Inject constructor(
    private val repository: EventsRepository
) {
    suspend fun execute(startDate: Long) = repository.deleteEventByStartDate(startDate)
}