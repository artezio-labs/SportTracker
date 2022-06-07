package com.artezio.osport.tracker.data.mappers

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.domain.model.LocationPointData
import com.mapbox.geojson.Point
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocationToPointMapperTest {

    @Test
    fun `test location to point object mapper`() {
        val mapper = LocationToPointMapper()
        assertEquals(
            Point.fromLngLat(0.0, 0.0, 0.0),
            mapper.map(LocationPointData(0.0, 0.0, 0.0, 0F, 0F, 0, 0, 0))
        )
        assertEquals(
            Point.fromLngLat(1.5, 5.1, 6.7),
            mapper.map(LocationPointData(5.1, 1.5, 6.7, 0F, 0F, 0, 0, 0))
        )
        assertEquals(
            Point.fromLngLat(67.2, 34.3, 24.3),
            mapper.map(LocationPointData(34.3, 67.2, 24.3, 0F, 0F, 0, 0, 0))
        )
    }
}