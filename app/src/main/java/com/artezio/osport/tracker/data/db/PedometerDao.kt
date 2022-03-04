package com.artezio.osport.tracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.artezio.osport.tracker.domain.model.PedometerData
import kotlinx.coroutines.flow.Flow

@Dao
interface PedometerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPedometerData(data: PedometerData)

    @Query("SELECT * FROM pedometer_data WHERE eventId = :eventId")
    fun getStepCount(eventId: Long): Flow<PedometerData>
}