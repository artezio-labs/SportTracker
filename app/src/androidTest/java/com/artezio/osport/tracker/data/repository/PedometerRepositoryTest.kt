package com.artezio.osport.tracker.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.data.db.PedometerDao
import com.artezio.osport.tracker.domain.model.PedometerData
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PedometerRepositoryTest {

    private lateinit var repository: com.artezio.osport.tracker.data.repository.PedometerRepository
    private lateinit var dao: PedometerDao
    private lateinit var pedometerData: PedometerData

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        repository = com.artezio.osport.tracker.data.repository.PedometerRepository(dao)
        pedometerData = mockk(relaxed = true)
    }

    @Test
    fun addPedometerData() = runBlocking {
        repository.addPedometerData(pedometerData)
        coVerify { dao.insertPedometerData(pedometerData) }
    }

    @Test
    fun getStepCount() = runBlocking {
        repository.getStepCount(1)
        coVerify { dao.getStepCount(1) }
    }

    @Test
    fun test_get_all_pedometer_data() = runBlocking {
        repository.getAllPedometerData()
        coVerify { dao.getAllPedometerData() }
    }

    @Test
    fun test_get_all_pedometer_data_flow() = runBlocking {
        repository.getAllPedometerDataFlow()
        coVerify { dao.getAllPedometerDataFlow() }
    }
}