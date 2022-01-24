package com.artezio.sporttracker.domain.repository

import com.artezio.sporttracker.domain.model.Event
import com.artezio.sporttracker.domain.model.LocationPointData
import com.artezio.sporttracker.domain.model.PedometerData
import com.artezio.sporttracker.domain.model.User

interface IRepository {

    interface IPedometerRepository {
        suspend fun addPedometerData(pedometerData: PedometerData)
    }

    interface ILocationRepository {
        suspend fun addLocationPointData(locationPointData: LocationPointData)
    }

    interface TrackNetworkRepository {
        // todo работа с сетью
    }

}