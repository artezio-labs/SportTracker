package com.artezio.osport.tracker.domain.repository

import com.artezio.osport.tracker.domain.model.*
import kotlinx.coroutines.flow.Flow

interface IRepository {

    interface IPedometerRepository {
        suspend fun addPedometerData(pedometerData: PedometerData)
        suspend fun getStepCount(eventId: Long): PedometerData
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
        suspend fun updateEvent(startDate: Long, name: String, trackingStateModel: TrackingStateModel)
        suspend fun updateEvent(id: Long, name: String, startDate: Long)
        fun getEventWithDataById(id: Long): Flow<EventWithData>
        fun getLastEventId(): Flow<Long>
        suspend fun deleteEvent(event: Event)
        suspend fun updateEvent(startDate: Long, trackingStateModel: TrackingStateModel)
        suspend fun deleteEventById(eventId: Long)
        suspend fun getLastEvent(): Event
        suspend fun updateEvent(event: Event)
        suspend fun insertTrackingState(trackingStateModel: TrackingStateModel)
        suspend fun getTrackingStateByEventId(eventId: Long): Flow<TrackingStateModel>
        suspend fun clearTrackingState()
    }

    interface IDataStoreRepository {
        suspend fun saveTrackingState(state: TrackingStateModel)
        suspend fun getTrackingState(): TrackingStateModel
    }

    interface TrackNetworkRepository {
        // todo работа с сетью
    }

}