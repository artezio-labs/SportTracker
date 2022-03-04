package com.artezio.osport.tracker.data.repository

import com.artezio.osport.tracker.data.prefs.DataStore
import com.artezio.osport.tracker.domain.model.TrackingStateModel
import com.artezio.osport.tracker.domain.repository.IRepository
import javax.inject.Inject

class DataStoreRepository @Inject constructor(
    private val dataStore: DataStore
) : IRepository.IDataStoreRepository {

    override suspend fun saveTrackingState(state: TrackingStateModel) {
        dataStore.saveTrackingState(state)
    }

    override suspend fun getTrackingState(): TrackingStateModel = dataStore.getTrackingState()


}