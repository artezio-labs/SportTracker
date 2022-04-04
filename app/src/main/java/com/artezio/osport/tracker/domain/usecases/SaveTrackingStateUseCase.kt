package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.EventsRepository
import com.artezio.osport.tracker.domain.model.TrackingStateModel
import javax.inject.Inject

class SaveTrackingStateUseCase @Inject constructor(
    private val repository: EventsRepository
) {
    suspend fun execute(state: TrackingStateModel) =
        repository.insertTrackingState(state)
}