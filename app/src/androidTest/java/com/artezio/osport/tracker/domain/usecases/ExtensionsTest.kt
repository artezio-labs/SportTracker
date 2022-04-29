package com.artezio.osport.tracker.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.domain.model.LocationPointData
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExtensionsTest {

    @Test
    fun distanceBetween() {
        assertEquals(
            distanceBetween(
                LocationPointData(0.0, 0.0, 0.0, 0F, 0F, 0L, 0, 0),
                LocationPointData(0.0, 0.0, 0.0, 0F, 0F, 0L, 0, 0)
            ),
            0F
        )
        assertEquals(
            distanceBetween(
                LocationPointData(10.0, 9.0, 0.0, 0F, 0F, 0L, 0, 0),
                LocationPointData(9.0, 8.0, 0.0, 0F, 0F, 0L, 0, 0)
            ),
            155851.5F
        )
        assertEquals(
            distanceBetween(
                LocationPointData(64.0, 10.0, 0.0, 0F, 0F, 0L, 0, 0),
                LocationPointData(9.0, 17.0, 0.0, 0F, 0F, 0L, 0, 0)
            ),
            6129825.0F
        )
    }
}