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
class GetLastEventIdUseCaseTest {

    private lateinit var useCase: GetLastEventIdUseCase
    private lateinit var repository: EventsRepository

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = GetLastEventIdUseCase(repository)
    }

    @Test
    fun execute() = runBlocking {
        useCase.execute()
        coVerify { repository.getLastEventId() }
    }
}