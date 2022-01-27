package com.artezio.sporttracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.artezio.sporttracker.domain.model.Event
import com.artezio.sporttracker.domain.model.EventWithData
import kotlinx.coroutines.flow.Flow

@Dao
interface EventsDao {

    @Insert
    suspend fun insertEvent(event: Event)

    @Transaction
    @Query("SELECT * FROM events")
    fun getAllEventsWithData(): Flow<List<EventWithData>>

}