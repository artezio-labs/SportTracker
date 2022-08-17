package com.artezio.osport.tracker.data.mappers

import android.location.Location
import com.artezio.osport.tracker.domain.model.LocationPointData

class LocationToLocationPointDataMapper : IMapper<Location, LocationPointData> {

    override fun map(obj: Location): LocationPointData {
        return LocationPointData(
            obj.latitude,
            obj.longitude,
            obj.altitude,
            obj.accuracy,
            obj.speed,
            obj.time,
            0,
            0
        )
    }
}