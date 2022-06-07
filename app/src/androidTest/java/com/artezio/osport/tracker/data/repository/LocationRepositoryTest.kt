package com.artezio.osport.tracker.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.data.db.LocationDao
import com.artezio.osport.tracker.domain.model.LocationPointData
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocationRepositoryTest {

    private lateinit var repository: com.artezio.osport.tracker.data.repository.LocationRepository
    private lateinit var dao: LocationDao
    private lateinit var location: LocationPointData

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        repository = com.artezio.osport.tracker.data.repository.LocationRepository(dao)
        location = mockk(relaxed = true)
    }

    @Test
    fun addLocationPointData() = runBlocking {
        repository.addLocationPointData(location)
        coVerify { dao.insertLocationData(location) }
    }

    @Test
    fun getAllLocationData() = runBlocking {
        repository.getAllLocationData()
        coVerify { dao.getAllLocationData() }
    }

    @Test
    fun getLocationsByEventId() = runBlocking {
        repository.getLocationsByEventId(1)
        coVerify { dao.getLocationsByEventId(1) }
    }

    @Test
    fun getAllLocationsById() = runBlocking {
        repository.getAllLocationsById(1)
        coVerify { dao.getAllLocationsById(1) }
    }
}