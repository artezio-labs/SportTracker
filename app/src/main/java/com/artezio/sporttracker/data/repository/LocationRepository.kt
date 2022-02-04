package com.artezio.sporttracker.data.repository

import com.artezio.sporttracker.data.db.LocationDao
import com.artezio.sporttracker.domain.model.LocationPointData
import com.artezio.sporttracker.domain.repository.IRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val dao: LocationDao
): IRepository.ILocationRepository {

    override suspend fun addLocationPointData(locationPointData: LocationPointData) {
        dao.insertLocationData(locationPointData)
    }

    override fun getAllLocationData(): Flow<List<LocationPointData>> =
        dao.getAllLocationData()

}