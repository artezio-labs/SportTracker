package com.artezio.osport.tracker.data.repository

import com.artezio.osport.tracker.data.db.PedometerDao
import com.artezio.osport.tracker.domain.model.PedometerData
import com.artezio.osport.tracker.domain.repository.IRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PedometerRepository @Inject constructor(
    private val dao: PedometerDao
): IRepository.IPedometerRepository {

    override suspend fun addPedometerData(pedometerData: PedometerData) {
        dao.insertPedometerData(pedometerData)
    }

    override fun getStepCount(eventId: Long): Flow<PedometerData> =
        dao.getStepCount(eventId)
}