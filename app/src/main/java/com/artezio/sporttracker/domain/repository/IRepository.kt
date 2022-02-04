package com.artezio.sporttracker.domain.repository

import com.artezio.sporttracker.domain.model.*
import kotlinx.coroutines.flow.Flow

interface IRepository {

    interface IPedometerRepository {
        suspend fun addPedometerData(pedometerData: PedometerData)
    }

    interface ILocationRepository {
        suspend fun addLocationPointData(locationPointData: LocationPointData)
        fun getAllLocationData(): Flow<List<LocationPointData>>
    }

    interface IEventsRepository {
        suspend fun addEvent(event: Event)
        fun getAllEvents(): Flow<List<EventWithData>>
        suspend fun getEventById(id: Long): Event
        suspend fun updateEvent(id: Long, name: String, startDate: Long)
        fun getEventWithDataById(id: Long): Flow<EventWithData>
    }

    interface TrackNetworkRepository {
        // todo работа с сетью
    }

}