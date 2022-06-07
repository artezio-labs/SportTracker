package com.artezio.osport.tracker.domain.usecases

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.data.repository.LocationRepository
import com.artezio.osport.tracker.domain.model.LocationPointData
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GetAllLocationDataUseCaseTest {

    private lateinit var usecase: GetAllLocationDataUseCase
    private lateinit var repository: LocationRepository

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        usecase = GetAllLocationDataUseCase(repository)
    }

    @Test
    fun execute() = runBlocking {
        usecase.execute()
        coVerifySequence {
            repository.getAllLocationData()
            flow<List<LocationPointData>> { }.map { }
            listOf<LocationPointData>().map { }
        }
    }
}