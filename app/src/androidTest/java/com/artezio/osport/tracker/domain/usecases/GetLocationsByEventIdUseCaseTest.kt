package com.artezio.osport.tracker.domain.usecases

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.data.mappers.LocationToPointMapper
import com.artezio.osport.tracker.data.repository.LocationRepository
import com.artezio.osport.tracker.domain.model.LocationPointData
import com.artezio.osport.tracker.presentation.tracker.AccuracyFactory
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GetLocationsByEventIdUseCaseTest {

    private lateinit var useCase: GetLocationsByEventIdUseCase
    private lateinit var repository: LocationRepository
    private lateinit var accuracyFactory: AccuracyFactory
    private lateinit var mapper: LocationToPointMapper


    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        accuracyFactory = AccuracyFactory()
        mapper = mockk(relaxed = true)
        useCase = GetLocationsByEventIdUseCase(repository, accuracyFactory, mapper)
    }

    @Test
    fun execute() = runBlocking {
        useCase.executeWithAccuracy(1)
        coVerifySequence {
            repository.getLocationsByEventId(1)
            flow<List<LocationPointData>> {  }.map {  }
            listOf<LocationPointData>().map { }
        }
    }
}