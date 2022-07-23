package com.artezio.osport.tracker.data.repository

import com.artezio.osport.tracker.data.db.EventsDao
import com.artezio.osport.tracker.domain.model.Event
import com.artezio.osport.tracker.domain.model.EventWithData
import com.artezio.osport.tracker.domain.model.PlannedEvent
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
        timerValue: Double
    ) {
        dao.updateEvent(
            startDate = startDate,
            eventName = name,
            timerValue = timerValue
        )
    }

    override suspend fun updatePlannedEvent(id: Long, event: PlannedEvent) {
        dao.updatePlannedEvent(id, event.name, event.startDate, event.duration)
    }

    override suspend fun updateEventName(name: String, startDate: Long) {
        dao.updateEventName(name, startDate)
    }

    override suspend fun getAllEventsList(): List<Event> =
        dao.getAllEvents()

    override suspend fun updateEvent(id: Long, name: String, startDate: Long) {
        dao.updateSpecificEventFields(id, name, startDate)
    }

    override fun getEventWithDataById(id: Long): Flow<EventWithData> =
        dao.getEventWithDataById(id)

    override fun getLastEventId(): Flow<Long> =
        dao.getLastEventId()

    override suspend fun getLastEvent(): Event = dao.getLastEvent()

    override suspend fun deleteEventByStartDate(startDate: Long) {
        dao.deleteEventByStartDate(startDate)
    }

    override suspend fun getPlannedEventById(id: Long): PlannedEvent =
        dao.getPlannedEventById(id)

    override suspend fun deletePlannedEventById(id: Long) {
        dao.deletePlannedEventById(id)
    }

    override fun getPlannedEvents(): Flow<List<PlannedEvent>> =
        dao.getAllPlannedEventsFlow()

    override suspend fun insertPlannedEvent(event: PlannedEvent) {
        dao.insertPlannedEvent(event)
    }

    override suspend fun getLastPlannedEvent(): PlannedEvent {
        return dao.getLastPlannedEvent()
    }

    override suspend fun getPlannedEventsList(): List<PlannedEvent> =
        dao.getAllPlannedEvents()
}