package com.artezio.sporttracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.artezio.sporttracker.domain.model.LocationPointData

@Dao
interface LocationDao {

    @Insert
    suspend fun insertLocationData(data: LocationPointData)

}