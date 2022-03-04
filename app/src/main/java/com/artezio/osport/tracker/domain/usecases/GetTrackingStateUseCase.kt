package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.DataStoreRepository
import javax.inject.Inject

class GetTrackingStateUseCase @Inject constructor(
    private val repository: DataStoreRepository
) {
    suspend fun execute() = repository.getTrackingState()
}