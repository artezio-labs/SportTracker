package com.artezio.osport.tracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.artezio.osport.tracker.domain.model.Event
import com.artezio.osport.tracker.domain.model.EventWithData
import com.artezio.osport.tracker.domain.model.PlannedEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface EventsDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertEvent(event: Event)

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Long): Event

    @Query("SELECT * FROM events")
    suspend fun getAllEvents(): List<Event>

    @Query(
        """
        UPDATE events 
        SET name = :name, startDate = :startDate
        WHERE id = :id
    """
    )
    suspend fun updateSpecificEventFields(
        id: Long,
        name: String,
        startDate: Long = 0L,
    )

    @Query(
        """
        UPDATE events 
        SET name = :name
        WHERE startDate = :startDate
    """
    )
    suspend fun updateEventName(
        name: String,
        startDate: Long = 0L,
    )

    @Query(
        """
        UPDATE events
        SET name = :eventName,
            timerValue = :timerValue
        WHERE startDate = :startDate
    """
    )
    suspend fun updateEvent(
        startDate: Long,
        eventName: String,
        timerValue: Double
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

    @Query("DELETE FROM events WHERE startDate = :startDate")
    suspend fun deleteEventByStartDate(startDate: Long)

    @Query(
        """
        SELECT * 
        FROM events
        WHERE id = (SELECT MAX(id) FROM events)
    """
    )
    suspend fun getLastEvent(): Event

    @Query("SELECT * FROM planned_events")
    fun getAllPlannedEventsFlow(): Flow<List<PlannedEvent>>

    @Query(
        """
        UPDATE planned_events
        SET name = :name,
            startDate = :startDate,
            endDate = :endDate
        WHERE id = :id
    """
    )
    fun updatePlannedEvent(id: Long, name: String, startDate: Long, endDate: Long)

    @Query("DELETE FROM planned_events WHERE id = :id")
    suspend fun deletePlannedEventById(id: Long)

    @Query("SELECT * FROM planned_events WHERE id = :id")
    suspend fun getPlannedEventById(id: Long): PlannedEvent

    @Insert(onConflict = REPLACE)
    suspend fun insertPlannedEvent(event: PlannedEvent)

    @Query(
        """
        SELECT * 
        FROM planned_events
        WHERE id = (SELECT MAX(id) FROM planned_events)
    """
    )
    suspend fun getLastPlannedEvent(): PlannedEvent

    @Query("SELECT * FROM planned_events")
    fun getAllPlannedEvents(): List<PlannedEvent>
}