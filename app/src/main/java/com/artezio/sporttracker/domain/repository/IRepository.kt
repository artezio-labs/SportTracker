package com.artezio.sporttracker.domain.repository

import com.artezio.sporttracker.domain.model.Event
import com.artezio.sporttracker.domain.model.LocationPointData
import com.artezio.sporttracker.domain.model.PedometerData
import com.artezio.sporttracker.domain.model.User

interface IRepository {

    interface TrackStorageRepository {
        suspend fun addUser(user: User)
        suspend fun addEvent(event: Event)
        suspend fun addPedometerData(pedometerData: PedometerData)
        suspend fun addLocationPointData(locationPointData: LocationPointData)


    }

    interface TrackNetworkRepository {
        // todo работа с сетью
    }

}