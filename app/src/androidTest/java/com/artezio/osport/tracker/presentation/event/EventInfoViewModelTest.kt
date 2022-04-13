package com.artezio.osport.tracker.presentation.event

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.domain.usecases.GetAllLocationsByIdUseCase
import com.artezio.osport.tracker.domain.usecases.GetEventByIdUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventInfoViewModelTest {

    private lateinit var viewModel: EventInfoViewModel
    private lateinit var getEventByIdUseCase: GetEventByIdUseCase
    private lateinit var getAllLocationsByIdUseCase: GetAllLocationsByIdUseCase

    @Before
    fun setUp() {
        getEventByIdUseCase = mockk(relaxed = true)
        getAllLocationsByIdUseCase = mockk(relaxed = true) {
            coEvery { execute(any()) } returns listOf()
        }
        viewModel = EventInfoViewModel(
            getEventByIdUseCase, getAllLocationsByIdUseCase
        )
    }

    @Test
    fun getEventById() {
        viewModel.getEventById(1)
        coVerify {
            getEventByIdUseCase.execute(1)
        }
    }

    @Test
    fun getDistanceByEventId() {
        viewModel.getDistanceByEventId(1)
        coVerify { getAllLocationsByIdUseCase.execute(1) }
    }

    @Test
    fun formatTime() {
        Assert.assertEquals("1 мин.".trim(), viewModel.formatTime(60.0).trim())
        Assert.assertEquals("1 сек.".trim(), viewModel.formatTime(1.0).trim())
        Assert.assertEquals("1 ч.".trim(), viewModel.formatTime(3600.0).trim())
        Assert.assertEquals("1 ч. 2 мин. 3 сек.".trim(), viewModel.formatTime(3723.0).trim())
    }
}