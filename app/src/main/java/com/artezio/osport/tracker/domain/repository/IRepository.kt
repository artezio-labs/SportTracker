package com.artezio.osport.tracker.domain.repository

import com.artezio.osport.tracker.domain.model.*
import kotlinx.coroutines.flow.Flow

interface IRepository {

    interface IPedometerRepository {
        suspend fun addPedometerData(pedometerData: PedometerData)
        suspend fun getStepCount(eventId: Long): PedometerData
        suspend fun getAllPedometerData(): List<PedometerData>
        fun getAllPedometerDataFlow(): Flow<List<PedometerData>>
    }

    interface ILocationRepository {
        suspend fun addLocationPointData(locationPointData: LocationPointData)
        fun getAllLocationData(): Flow<List<LocationPointData>>
        fun getLocationsByEventId(id: Long): Flow<List<LocationPointData>>
        suspend fun getAllLocationsById(id: Long): List<LocationPointData>
    }

    interface IEventsRepository {
        suspend fun addEvent(event: Event)
        fun getAllEvents(): Flow<List<EventWithData>>
        suspend fun getEventById(id: Long): Event
        suspend fun updateEvent(
            startDate: Long,
            name: String,
            timerValue: Double
        )

        fun getEventWithDataById(id: Long): Flow<EventWithData>
        fun getLastEventId(): Flow<Long>
        suspend fun getLastEvent(): Event
        suspend fun deleteEventByStartDate(startDate: Long)
        suspend fun updateEventName(name: String, startDate: Long)
        suspend fun updateEvent(id: Long, name: String, startDate: Long)
        suspend fun getAllEventsList(): List<Event>
        suspend fun updatePlannedEvent(id: Long, event: PlannedEvent)
        suspend fun getPlannedEventById(id: Long): PlannedEvent
        suspend fun deletePlannedEventById(id: Long)
        fun getPlannedEvents(): Flow<List<PlannedEvent>>
        suspend fun insertPlannedEvent(event: PlannedEvent)
        suspend fun getLastPlannedEvent(): PlannedEvent
        suspend fun getPlannedEventsList(): List<PlannedEvent>
    }

    interface TrackNetworkRepository {
        // todo работа с сетью
    }

}