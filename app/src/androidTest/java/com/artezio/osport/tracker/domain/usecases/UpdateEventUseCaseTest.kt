package com.artezio.osport.tracker.domain.usecases

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.data.repository.EventsRepository
import com.artezio.osport.tracker.domain.model.Event
import com.artezio.osport.tracker.domain.model.TrackingStateModel
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpdateEventUseCaseTest {

    private lateinit var useCase: UpdateEventUseCase
    private lateinit var repository: EventsRepository
    private lateinit var event: Event
    private lateinit var trackingState: TrackingStateModel

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = UpdateEventUseCase(repository)
        event = mockk()
        trackingState = mockk()
    }

    @Test
    fun executeUpdateWithNameAndStartDate() = runBlocking {
        useCase.execute(1, "Тест", 0)
        coVerify { repository.updateEvent(1, "Тест", 0) }
    }

    @Test
    fun executeUpdateWithTrackingState() = runBlocking {
        useCase.execute(0, "Тест", trackingState)
        coVerify { repository.updateEvent(0, "Тест", trackingState) }
    }
}