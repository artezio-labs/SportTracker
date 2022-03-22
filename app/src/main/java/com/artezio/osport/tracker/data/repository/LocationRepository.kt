package com.artezio.osport.tracker.data.repository

import com.artezio.osport.tracker.data.db.LocationDao
import com.artezio.osport.tracker.domain.model.LocationPointData
import com.artezio.osport.tracker.domain.repository.IRepository
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

    override fun getLocationsByEventId(id: Long): Flow<List<LocationPointData>> =
        dao.getLocationsByEventId(id)

    override suspend fun getAllLocationsById(id: Long): List<LocationPointData> = dao.getAllLocationsById(id)

}