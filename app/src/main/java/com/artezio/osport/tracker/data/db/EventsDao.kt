package com.artezio.osport.tracker.data.db

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.artezio.osport.tracker.domain.model.Event
import com.artezio.osport.tracker.domain.model.EventWithData
import kotlinx.coroutines.flow.Flow

@Dao
interface EventsDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertEvent(event: Event)

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Long): Event

    @Update
    suspend fun updateEvent(event: Event)

    @Query(
        """
        UPDATE events 
        SET name = :name, startDate = :startDate, endDate = :endDate
        WHERE id = :id
    """
    )
    suspend fun updateSpecificEventFields(
        id: Long,
        name: String,
        startDate: Long = 0L,
        endDate: Long?
    )

    @Query(
        """
        UPDATE events
        SET endDate = :endDate,
            name = :eventName,
            timerValue = :timerValue, 
            speedValue = :speedValue,
            stepsValue = :stepsValue,
            gpsPointsValue = :gpsPointsValue
        WHERE id = :eventId
    """
    )
    suspend fun updateEventsEndDateAndTrackingState(
        eventId: Long,
        eventName: String,
        endDate: Long,
        timerValue: Double,
        speedValue: Double,
        stepsValue: Int,
        gpsPointsValue: Int
    )

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

    @Delete
    fun deleteEvent(event: Event)

    @Query("DELETE FROM events where id = :eventId")
    fun deleteEventById(eventId: Long)

    @Query("""
        SELECT * 
        FROM events
        WHERE id = (SELECT MAX(id) FROM events)
    """)
    suspend fun getLastEvent(): Event
}