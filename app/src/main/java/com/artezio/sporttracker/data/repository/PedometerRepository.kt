package com.artezio.sporttracker.data.repository

import com.artezio.sporttracker.data.db.PedometerDao
import com.artezio.sporttracker.domain.model.PedometerData
import com.artezio.sporttracker.domain.repository.IRepository
import javax.inject.Inject

class PedometerRepository @Inject constructor(
    private val dao: PedometerDao
): IRepository.IPedometerRepository {
    override suspend fun addPedometerData(pedometerData: PedometerData) {
        dao.insertPedometerData(pedometerData)
    }
}