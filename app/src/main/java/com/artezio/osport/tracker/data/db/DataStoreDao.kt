package com.artezio.osport.tracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.artezio.osport.tracker.domain.model.TrackingStateModel

@Dao
interface DataStoreDao {

    @Insert
    fun saveState(state: TrackingStateModel)

    @Query("""
        SELECT * FROM tracking_state
        WHERE eventId = :eventId
    """)
    fun getStateById(eventId: Long): TrackingStateModel
}