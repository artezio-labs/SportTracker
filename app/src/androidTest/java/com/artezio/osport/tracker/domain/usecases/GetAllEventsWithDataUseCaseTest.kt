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
class GetAllEventsWithDataUseCaseTest {
    private lateinit var usecase: GetAllEventsWithDataUseCase
    private lateinit var repository: EventsRepository

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        usecase = GetAllEventsWithDataUseCase(repository)
    }

    @Test
    fun execute() = runBlocking {
        usecase.execute()
        coVerify { repository.getAllEvents() }
    }
}