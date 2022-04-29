package com.artezio.osport.tracker.domain.usecases

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.data.repository.LocationRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GetAllLocationsByIdUseCaseTest {

    private lateinit var useCase: GetAllLocationsByIdUseCase
    private lateinit var repository: LocationRepository

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = GetAllLocationsByIdUseCase(repository)
    }

    @Test
    fun execute() = runBlocking {
        useCase.execute(1)
        coVerify { repository.getAllLocationsById(1) }
    }
}