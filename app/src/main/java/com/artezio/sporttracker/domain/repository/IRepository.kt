package com.artezio.sporttracker.domain.repository

import com.artezio.sporttracker.domain.model.*
import kotlinx.coroutines.flow.Flow

interface IRepository {

    interface IPedometerRepository {
        suspend fun addPedometerData(pedometerData: PedometerData)
    }

    interface ILocationRepository {
        suspend fun addLocationPointData(locationPointData: LocationPointData)
    }

    interface IEventsRepository {
        suspend fun addEvent(event: Event)
        fun getAllEvents(): Flow<List<EventWithData>>
    }

    interface TrackNetworkRepository {
        // todo работа с сетью
    }

}