package com.artezio.sporttracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.artezio.sporttracker.domain.model.EventWithData
import com.artezio.sporttracker.domain.model.EventWithLocations
import com.artezio.sporttracker.domain.model.LocationPointData
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert
    suspend fun insertLocationData(data: LocationPointData)

    @Query("SELECT * FROM location_data")
    fun getAllLocationData(): Flow<List<LocationPointData>>


    @Query("SELECT * FROM location_data WHERE eventId = :id")
    fun getLocationsByEventId(id: Long): Flow<List<LocationPointData>>
}