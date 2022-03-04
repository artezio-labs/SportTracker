package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.PedometerRepository
import javax.inject.Inject

class GetStepCountUseCase @Inject constructor(
    private val repository: PedometerRepository
) {
    fun execute(eventId: Long) = repository.getStepCount(eventId)
}