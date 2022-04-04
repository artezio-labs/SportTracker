package com.artezio.osport.tracker.data.repository

import com.artezio.osport.tracker.data.db.EventsDao
import com.artezio.osport.tracker.domain.model.Event
import com.artezio.osport.tracker.domain.model.EventWithData
import com.artezio.osport.tracker.domain.model.TrackingStateModel
import com.artezio.osport.tracker.domain.repository.IRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EventsRepository @Inject constructor(
    private val dao: EventsDao
) : IRepository.IEventsRepository {
    override fun getAllEvents(): Flow<List<EventWithData>> =
        dao.getAllEventsWithData()

    override suspend fun addEvent(event: Event) {
        dao.insertEvent(event)
    }

    override suspend fun getEventById(id: Long): Event =
        dao.getEventById(id)

    override suspend fun updateEvent(
        startDate: Long,
        name: String,
        trackingStateModel: TrackingStateModel
    ) {
        dao.updateEvent(
            startDate = startDate,
            eventName = name,
            endDate = System.currentTimeMillis(),
            timerValue = trackingStateModel.timerValue,
            speedValue = trackingStateModel.speedValue,
            stepsValue = trackingStateModel.stepsValue,
            gpsPointsValue = trackingStateModel.gpsPointsValue
        )
    }

    override suspend fun updateEvent(id: Long, name: String, startDate: Long) {
        dao.updateSpecificEventFields(id, name, startDate, null)
    }

    override suspend fun updateEvent(
        startDate: Long,
        trackingStateModel: TrackingStateModel
    ) {
        dao.updateEventsEndDateAndTrackingState(
            startDate = startDate,
            endDate = System.currentTimeMillis(),
            timerValue = trackingStateModel.timerValue,
            speedValue = trackingStateModel.speedValue,
            stepsValue = trackingStateModel.stepsValue,
            gpsPointsValue = trackingStateModel.gpsPointsValue
        )
    }

    override suspend fun updateEvent(event: Event) = dao.updateEvent(event)

    override fun getEventWithDataById(id: Long): Flow<EventWithData> =
        dao.getEventWithDataById(id)

    override fun getLastEventId(): Flow<Long> =
        dao.getLastEventId()

    override suspend fun deleteEvent(event: Event) {
        dao.deleteEvent(event)
    }

    override suspend fun deleteEventById(eventId: Long) {
        dao.deleteEventById(eventId)
    }

    override suspend fun getLastEvent(): Event = dao.getLastEvent()

    override suspend fun insertTrackingState(trackingStateModel: TrackingStateModel) {
        dao.saveTrackingState(trackingStateModel)
    }

    override suspend fun getTrackingStateByEventId(eventId: Long): Flow<TrackingStateModel> =
        dao.getTrackingStateByEventId(eventId)

    override suspend fun clearTrackingState() {
        dao.clearTrackingState()
    }

}