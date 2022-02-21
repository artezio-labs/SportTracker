package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.EventsRepository
import javax.inject.Inject

class GetEventWithDataByIdUseCase @Inject constructor(
    private val repository: EventsRepository
) {

    fun execute(id: Long) = repository.getEventWithDataById(id)

}