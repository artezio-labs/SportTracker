package com.artezio.sporttracker.domain.usecases

import com.artezio.sporttracker.data.repository.EventsRepository
import javax.inject.Inject

class GetEventWithDataByIdUseCase @Inject constructor(
    private val repository: EventsRepository
) {

    fun execute(id: Long) = repository.getEventWithDataById(id)

}