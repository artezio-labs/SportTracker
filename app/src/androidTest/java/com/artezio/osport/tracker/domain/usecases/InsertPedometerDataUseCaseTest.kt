package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.PedometerRepository
import com.artezio.osport.tracker.domain.model.PedometerData
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class InsertPedometerDataUseCaseTest {

    private lateinit var repository: PedometerRepository
    private lateinit var useCase: InsertPedometerDataUseCase
    private lateinit var pedometerData: PedometerData

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = InsertPedometerDataUseCase(repository)
        pedometerData = mockk()
    }

    @Test
    fun execute() = runBlocking {
        useCase.execute(pedometerData)
        coVerify { repository.addPedometerData(pedometerData) }
    }
}