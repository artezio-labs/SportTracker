package com.artezio.osport.tracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.artezio.osport.tracker.domain.model.LocationPointData
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationData(data: LocationPointData)

    @Query("SELECT * FROM location_data")
    fun getAllLocationData(): Flow<List<LocationPointData>>


    @Query("SELECT * FROM location_data WHERE eventId = :id")
    fun getLocationsByEventId(id: Long): Flow<List<LocationPointData>>
}