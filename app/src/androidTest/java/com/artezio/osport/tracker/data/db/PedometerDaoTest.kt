package com.artezio.osport.tracker.data.db

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.artezio.osport.tracker.domain.model.PedometerData
import junit.framework.Assert.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PedometerDaoTest {

    private lateinit var db: TrackerDb
    private lateinit var dao: PedometerDao
    private lateinit var testPedometerData: PedometerData

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            TrackerDb::class.java
        ).build()
        dao = db.pedometerDao()
        testPedometerData = PedometerData(18, System.currentTimeMillis(), 1)
    }

    @Test
    fun insertPedometerData() = runBlocking {
        dao.insertPedometerData(testPedometerData)
        assertEquals(dao.getStepCount(1), testPedometerData)
    }

    @Test
    fun getStepCount() = runBlocking {
        assertNull(dao.getStepCount(1))
        dao.insertPedometerData(testPedometerData)
        assertNotNull(dao.getStepCount(1))
    }

    @After
    fun tearDown() {
        db.close()
    }
}