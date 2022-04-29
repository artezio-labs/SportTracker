package com.artezio.osport.tracker.domain.usecases

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.data.repository.EventsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GetEventByIdUseCaseTest {

    private lateinit var useCase: GetEventByIdUseCase
    private lateinit var repository: EventsRepository

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = GetEventByIdUseCase(repository)
    }

    @Test
    fun execute() = runBlocking {
        useCase.execute(1)
        coVerify { repository.getEventById(1) }
    }
}