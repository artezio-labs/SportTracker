package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.DataStoreRepository
import com.artezio.osport.tracker.domain.model.TrackingStateModel
import javax.inject.Inject

class SaveTrackingStateUseCase @Inject constructor(
    private val repository: DataStoreRepository
) {
    suspend fun execute(state: TrackingStateModel) =
        repository.saveTrackingState(state)
}