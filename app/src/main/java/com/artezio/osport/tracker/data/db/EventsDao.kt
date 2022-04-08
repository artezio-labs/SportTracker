package com.artezio.osport.tracker.data.db

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.artezio.osport.tracker.domain.model.Event
import com.artezio.osport.tracker.domain.model.EventWithData
import com.artezio.osport.tracker.domain.model.TrackingStateModel
import kotlinx.coroutines.flow.Flow

@Dao
interface EventsDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertEvent(event: Event)

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Long): Event

    @Update(onConflict = REPLACE)
    suspend fun updateEvent(event: Event)

    @Query("SELECT * FROM events")
    suspend fun getAllEvents(): List<Event>

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
            timerValue = :timerValue, 
            speedValue = :speedValue,
            stepsValue = :stepsValue,
            gpsPointsValue = :gpsPointsValue
        WHERE startDate = :startDate
    """
    )
    suspend fun updateEventsEndDateAndTrackingState(
        startDate: Long,
        endDate: Long,
        timerValue: Double,
        speedValue: Double,
        stepsValue: Int,
        gpsPointsValue: Int
    )

    @Query(
        """
        UPDATE events
        SET name = :eventName,
            endDate = :endDate,
            timerValue = :timerValue, 
            speedValue = :speedValue,
            stepsValue = :stepsValue,
            gpsPointsValue = :gpsPointsValue
        WHERE startDate = :startDate
    """
    )
    suspend fun updateEvent(
        startDate: Long,
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

    @Query("DELETE FROM events WHERE startDate = :startDate")
    suspend fun deleteEventByStartDate(startDate: Long)

    @Query("DELETE FROM events where id = :eventId")
    fun deleteEventById(eventId: Long)

    @Query(
        """
        SELECT * 
        FROM events
        WHERE id = (SELECT MAX(id) FROM events)
    """
    )
    suspend fun getLastEvent(): Event

    @Insert(onConflict = REPLACE)
    suspend fun saveTrackingState(trackingStateModel: TrackingStateModel)

    @Query("SELECT * FROM tracking_state WHERE eventId = :eventId")
    fun getTrackingStateByEventId(eventId: Long): Flow<TrackingStateModel>

    @Query("DELETE FROM tracking_state")
    suspend fun clearTrackingState()
}