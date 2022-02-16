package com.artezio.sporttracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.artezio.sporttracker.domain.model.PedometerData

@Dao
interface PedometerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPedometerData(data: PedometerData)
}