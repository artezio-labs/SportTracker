package com.artezio.osport.tracker.presentation.event

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.domain.usecases.GetAllLocationsByIdUseCase
import com.artezio.osport.tracker.domain.usecases.GetEventByIdUseCase
import com.artezio.osport.tracker.domain.usecases.GetEventInfoUseCase
import com.artezio.osport.tracker.domain.usecases.UpdateEventNameUseCase
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventInfoViewModelTest {

    private lateinit var viewModel: EventInfoViewModel
    private lateinit var getAllLocationsByIdUseCase: GetAllLocationsByIdUseCase
    private lateinit var getEventInfoUseCase: GetEventInfoUseCase
    private lateinit var updateEventNameUseCase: UpdateEventNameUseCase
    private lateinit var getEventByIdUseCase: GetEventByIdUseCase

    @Before
    fun setUp() {
        getAllLocationsByIdUseCase = mockk(relaxed = true)
        getEventInfoUseCase = mockk(relaxed = true)
        updateEventNameUseCase = mockk(relaxed = true)
        getEventByIdUseCase = mockk(relaxed = true)
        viewModel = EventInfoViewModel(
            getEventInfoUseCase,
            getAllLocationsByIdUseCase,
            updateEventNameUseCase,
            getEventByIdUseCase
        )
    }

    @Test
    fun test_getEventInfoUseCase() {
        viewModel.getEventInfo(1)
        coVerify { getEventInfoUseCase.execute(1) }
    }

    @Test
    fun test_getLocationsByIdUseCase() {
        viewModel.getLocationsById(1)
        coVerify { getAllLocationsByIdUseCase.execute(1) }
    }
}