package com.artezio.osport.tracker.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.domain.model.LocationPointData
import com.artezio.osport.tracker.domain.model.PedometerData
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventInfoUtilTest {

    private val testPedometerData = listOf(
        PedometerData(4, 3000000, 0L),
        PedometerData(6, 3009934, 0L),
        PedometerData(3, 6009934, 0L),
        PedometerData(8, 8802937, 0L),
    )
    private val testLocationData = listOf(
        LocationPointData(1.0, 3.0, 0.0, 0F, 34.5F, 0, 0, 0),
        LocationPointData(3.0, 6.0, 0.0, 0F, 21F, 0, 0, 0),
        LocationPointData(6.0, 3.0, 0.0, 0F, 56F, 0, 0, 0),
        LocationPointData(5.0, 40.0, 0.0, 0F, 87F, 0, 0, 0),
        LocationPointData(8.0, 6.0, 0.0, 0F, 34F, 0, 0, 0),
    )

    @Test
    fun `test calculate average cadence`() = runBlocking {
        assertEquals(1, EventInfoUtils.calculateCadence(testPedometerData))
        assertEquals(0, EventInfoUtils.calculateCadence(emptyList()))
        assertEquals(4, EventInfoUtils.calculateCadence(testPedometerData.take(1)))
    }

    @Test
    fun `test calculate distance`() = runBlocking {
        assertEquals(0.0, EventInfoUtils.calculateDistance(emptyList()))
        assertEquals(8745200.84375, EventInfoUtils.calculateDistance(testLocationData))
    }

    @Test
    fun `test calculate average speed`() = runBlocking {
        assertEquals(0.0, EventInfoUtils.calculateAvgSpeed(emptyList()))
        assertEquals(46.5, EventInfoUtils.calculateAvgSpeed(testLocationData))
    }

    @Test
    fun `test filter data`() = runBlocking {
        assertEquals(emptyList<PedometerData>(), EventInfoUtils.filterData(0L, emptyList()))
        assertEquals(emptyList<PedometerData>(), EventInfoUtils.filterData(1L, testPedometerData))
        assertEquals(
            listOf(PedometerData(stepCount = 4, time = 3000000, eventId = 0)),
            EventInfoUtils.filterData(0L, testPedometerData)
        )
    }
}