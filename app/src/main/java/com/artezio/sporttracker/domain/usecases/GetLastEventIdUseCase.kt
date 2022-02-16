package com.artezio.sporttracker.domain.usecases

import com.artezio.sporttracker.data.repository.EventsRepository
import javax.inject.Inject

class GetLastEventIdUseCase @Inject constructor(
    private val repository: EventsRepository
) {

    fun execute() = repository.getLastEventId()

}