package com.artezio.sporttracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.artezio.sporttracker.domain.model.LocationPointData
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert
    suspend fun insertLocationData(data: LocationPointData)

    @Query("SELECT * FROM location_data")
    fun getAllLocationData(): Flow<List<LocationPointData>>

}