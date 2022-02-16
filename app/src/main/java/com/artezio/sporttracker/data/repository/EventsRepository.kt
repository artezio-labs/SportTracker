package com.artezio.sporttracker.data.repository

import com.artezio.sporttracker.data.db.EventsDao
import com.artezio.sporttracker.domain.model.Event
import com.artezio.sporttracker.domain.model.EventWithData
import com.artezio.sporttracker.domain.repository.IRepository
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

    override suspend fun updateEvent(id: Long, name: String, startDate: Long) {
        dao.updateSpecificEventFields(id, name, startDate)
    }

    override fun getEventWithDataById(id: Long): Flow<EventWithData> =
        dao.getEventWithDataById(id)

    override fun getLastEventId(): Flow<Long> =
        dao.getLastEventId()
}