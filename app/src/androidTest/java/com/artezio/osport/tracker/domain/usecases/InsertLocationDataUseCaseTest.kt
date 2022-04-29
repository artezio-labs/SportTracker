package com.artezio.osport.tracker.domain.usecases

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.data.repository.LocationRepository
import com.artezio.osport.tracker.domain.model.LocationPointData
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InsertLocationDataUseCaseTest {

    private lateinit var useCase: InsertLocationDataUseCase
    private lateinit var repository: LocationRepository
    private lateinit var location: LocationPointData

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = InsertLocationDataUseCase(repository)
        location = mockk()
    }

    @Test
    fun execute() = runBlocking {
        useCase.execute(location)
        coVerify { repository.addLocationPointData(location) }
    }
}