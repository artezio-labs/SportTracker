package com.artezio.osport.tracker.domain.usecases

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.data.repository.EventsRepository
import com.artezio.osport.tracker.domain.model.Event
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InsertEventUseCaseTest {

    private lateinit var useCase: InsertEventUseCase
    private lateinit var repository: EventsRepository
    private lateinit var event: Event

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = InsertEventUseCase(repository)
        event = mockk()
    }

    @Test
    fun execute() = runBlocking {
        useCase.execute(event)
        coVerify { repository.addEvent(event) }
    }
}