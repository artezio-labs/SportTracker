package com.artezio.osport.tracker.data.mappers

import com.artezio.osport.tracker.domain.model.LocationPointData
import com.mapbox.geojson.Point

class LocationToPointMapper : IMapper<LocationPointData, Point> {
    override fun map(obj: LocationPointData): Point {
        return Point.fromLngLat(obj.longitude, obj.latitude, obj.altitude)
    }
}