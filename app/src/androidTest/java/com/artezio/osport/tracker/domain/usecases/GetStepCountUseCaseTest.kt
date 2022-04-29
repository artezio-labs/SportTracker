package com.artezio.osport.tracker.domain.usecases

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.data.repository.PedometerRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GetStepCountUseCaseTest {

    private lateinit var repository: PedometerRepository
    private lateinit var useCase: GetStepCountUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = GetStepCountUseCase(repository)
    }

    @Test
    fun execute() = runBlocking {
        useCase.execute(1)
        coVerify { repository.getStepCount(1) }
    }
}