package com.artezio.osport.tracker.presentation.event

import com.artezio.osport.tracker.data.gpx.GPX
import com.artezio.osport.tracker.domain.usecases.*
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Before
import org.junit.Test


class EventInfoViewModelTest {

    private lateinit var viewModel: EventInfoViewModel
    private lateinit var getAllLocationsByIdUseCase: GetAllLocationsByIdUseCase
    private lateinit var getEventInfoUseCase: GetEventInfoUseCase
    private lateinit var updateEventNameUseCase: UpdateEventNameUseCase
    private lateinit var getEventByIdUseCase: GetEventByIdUseCase
    private lateinit var deleteEventUseCase: DeleteEventUseCase
    private lateinit var gpx: GPX

    @Before
    fun setUp() {
        getAllLocationsByIdUseCase = mockk(relaxed = true)
        getEventInfoUseCase = mockk(relaxed = true)
        updateEventNameUseCase = mockk(relaxed = true)
        getEventByIdUseCase = mockk(relaxed = true)
        deleteEventUseCase = mockk(relaxed = true)
        gpx = mockk(relaxed = true)
        viewModel = EventInfoViewModel(
            getEventInfoUseCase,
            getAllLocationsByIdUseCase,
            updateEventNameUseCase,
            getEventByIdUseCase,
            deleteEventUseCase,
            gpx
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