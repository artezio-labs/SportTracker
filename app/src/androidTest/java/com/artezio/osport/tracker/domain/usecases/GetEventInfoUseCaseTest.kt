package com.artezio.osport.tracker.domain.usecases

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.data.repository.EventsRepository
import com.artezio.osport.tracker.data.repository.LocationRepository
import com.artezio.osport.tracker.data.repository.PedometerRepository
import com.artezio.osport.tracker.util.ResourceProvider
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GetEventInfoUseCaseTest {

    private lateinit var useCase: GetEventInfoUseCase
    private lateinit var eventsRepository: EventsRepository
    private lateinit var locationsRepository: LocationRepository
    private lateinit var pedometerRepository: PedometerRepository
    private lateinit var resourceProvider: ResourceProvider

    @Before
    fun setUp() {
        eventsRepository = mockk(relaxed = true)
        locationsRepository = mockk(relaxed = true)
        pedometerRepository = mockk(relaxed = true)
        resourceProvider = mockk(relaxed = true)
        useCase = GetEventInfoUseCase(
            eventsRepository,
            locationsRepository,
            pedometerRepository,
            resourceProvider
        )
    }

    @Test
    fun execute() = runBlocking {
        useCase.execute(TEST_EVENT_ID)
        coVerifySequence {
            eventsRepository.getEventById(TEST_EVENT_ID)
            locationsRepository.getAllLocationsById(TEST_EVENT_ID)
            pedometerRepository.getStepCount(TEST_EVENT_ID)
            pedometerRepository.getAllPedometerData()
        }
    }

    companion object {
        private const val TEST_EVENT_ID = 1L
    }
}