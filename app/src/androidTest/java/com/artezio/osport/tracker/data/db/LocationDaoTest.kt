package com.artezio.osport.tracker.data.db

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.artezio.osport.tracker.domain.model.LocationPointData
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class LocationDaoTest {

    private lateinit var db: TrackerDb
    private lateinit var dao: LocationDao
    private lateinit var testLocation: LocationPointData

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            TrackerDb::class.java
        ).build()
        dao = db.locationDao()
        testLocation = LocationPointData(
            latitude = 14.8,
            longitude = 123.7,
            altitude = 34.6,
            accuracy = 10.5F,
            14F,
            34938273726,
            50,
            1
        )
    }

    @Test
    fun insertLocationData(): Unit = runBlocking {
        dao.insertLocationData(testLocation)
        dao.getAllLocationData().map {
            assertTrue(!it.isNullOrEmpty())
        }
    }

    @Test
    fun getAllLocationsData(): Unit = runBlocking {
        val locations = mutableListOf<LocationPointData>()
        for (i in 0 until 5) {
            locations.add(
                testLocation.copy(
                    latitude = Random.nextDouble(1.0, 100.0),
                    longitude = Random.nextDouble(1.0, 100.0),
                    eventId = Random.nextLong(1, 20)
                )
            )
            dao.insertLocationData(locations[i])
        }
        dao.getAllLocationData().map {
            locations.forEachIndexed { index, locationPointData ->
                assertEquals(it[index], locationPointData)
            }
        }
    }

    @Test
    fun getAllLocationsById(): Unit = runBlocking {
        for (i in 0 until 5) {
            dao.insertLocationData(
                testLocation.copy(
                    latitude = Random.nextDouble(1.0, 100.0),
                    longitude = Random.nextDouble(1.0, 100.0)
                )
            )
        }
        dao.insertLocationData(
            testLocation.copy(
                latitude = Random.nextDouble(1.0, 100.0),
                longitude = Random.nextDouble(1.0, 100.0),
                eventId = 2
            )
        )
        dao.insertLocationData(
            testLocation.copy(
                latitude = Random.nextDouble(1.0, 100.0),
                longitude = Random.nextDouble(1.0, 100.0),
                eventId = 2
            )
        )
        dao.insertLocationData(
            testLocation.copy(
                latitude = Random.nextDouble(1.0, 100.0),
                longitude = Random.nextDouble(1.0, 100.0),
                eventId = 3
            )
        )
        dao.insertLocationData(
            testLocation.copy(
                latitude = Random.nextDouble(1.0, 100.0),
                longitude = Random.nextDouble(1.0, 100.0),
                eventId = 5
            )
        )
        assert(dao.getAllLocationsById(1).size == 5)
        assert(dao.getAllLocationsById(2).size == 2)
        assert(dao.getAllLocationsById(3).size == 1)
        assert(dao.getAllLocationsById(5).size == 1)
    }

    @Test
    fun getAllLocationsByIdFlow(): Unit = runBlocking {
        for (i in 0 until 5) {
            dao.insertLocationData(
                testLocation.copy(
                    latitude = Random.nextDouble(1.0, 100.0),
                    longitude = Random.nextDouble(1.0, 100.0)
                )
            )
        }
        dao.getLocationsByEventId(1).map {
            it.forEach { location ->
                assertTrue(location.eventId == 1L)
            }
        }
    }

    @After
    fun tearDown() {
        db.close()
    }
}