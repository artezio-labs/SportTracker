package com.artezio.sporttracker.data.db

import androidx.room.*
import com.artezio.sporttracker.domain.model.Event
import com.artezio.sporttracker.domain.model.EventWithData
import kotlinx.coroutines.flow.Flow

@Dao
interface EventsDao {

    @Insert
    suspend fun insertEvent(event: Event)

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Long): Event

    @Update
    suspend fun updateEvent(event: Event)

    @Query("""
        UPDATE events 
        SET name = :name, startDate = :startDate
        WHERE id = :id
    """)
    suspend fun updateSpecificEventFields(id: Long, name: String, startDate: Long)

    @Transaction
    @Query("SELECT * FROM events")
    fun getAllEventsWithData(): Flow<List<EventWithData>>

    @Transaction
    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventWithDataById(id: Long): Flow<EventWithData>

    @Query("""
        SELECT id 
        FROM events 
        ORDER BY id DESC 
        LIMIT 1
    """)
    fun getLastEventId(): Flow<Long>

}