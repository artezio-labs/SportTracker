package com.artezio.osport.tracker.data.db

import androidx.room.*
import com.artezio.osport.tracker.domain.model.Event
import com.artezio.osport.tracker.domain.model.EventWithData
import kotlinx.coroutines.flow.Flow

@Dao
interface EventsDao {
    @Insert
    suspend fun insertEvent(event: Event)

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Long): Event

    @Update
    suspend fun updateEvent(event: Event)

    @Query(
        """
        UPDATE events 
        SET name = :name, startDate = :startDate
        WHERE id = :id
    """
    )
    suspend fun updateSpecificEventFields(id: Long, name: String, startDate: Long)

    @Transaction
    @Query("SELECT * FROM events")
    fun getAllEventsWithData(): Flow<List<EventWithData>>

    @Transaction
    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventWithDataById(id: Long): Flow<EventWithData>

    @Query(
        """
        SELECT CASE
                    WHEN COUNT(*) = 0
                        THEN 0
                    ELSE MAX(id)
                    END id
        FROM events
    """
    )
    fun getLastEventId(): Flow<Long>
}