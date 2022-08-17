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

    override suspend fun getStepCount(eventId: Long): PedometerData =
        dao.getStepCount(eventId)

    override suspend fun getAllPedometerData(): List<PedometerData> =
        dao.getAllPedometerData()

    override fun getAllPedometerDataFlow(): Flow<List<PedometerData>> =
        dao.getAllPedometerDataFlow()

    override fun getAllPedometerDataFlowById(eventId: Long): Flow<List<PedometerData>> =
        dao.getAllPedometerDataFlow(eventId)
}