package com.artezio.osport.tracker.presentation.event

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.domain.usecases.*
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SaveEventViewModelTest {

    private lateinit var viewModel: SaveEventViewModel

    private val testIoScope = CoroutineScope(Dispatchers.IO)

    private lateinit var deleteEventUseCase: DeleteEventUseCase
    private lateinit var getAllLocationsByIdUseCase: GetAllLocationsByIdUseCase
    private lateinit var getEventByIdUseCase: GetEventByIdUseCase
    private lateinit var getLastEventUseCase: GetLastEventUseCase
    private lateinit var getStepCountUseCase: GetStepCountUseCase
    private lateinit var updateEventUseCase: UpdateEventUseCase

    @Before
    fun setUp() {
        deleteEventUseCase = mockk(relaxed = true)
        getAllLocationsByIdUseCase = mockk(relaxed = true)
        getEventByIdUseCase = mockk(relaxed = true)
        getLastEventUseCase = mockk(relaxed = true)
        getStepCountUseCase = mockk(relaxed = true)
        updateEventUseCase = mockk(relaxed = true)
        viewModel = SaveEventViewModel(
            deleteEventUseCase,
            getLastEventUseCase,
            getEventByIdUseCase,
            getAllLocationsByIdUseCase,
            getStepCountUseCase,
            updateEventUseCase
        )
    }

    @Test
    fun whenDeleteEventIsCalledShouldDeleteJustAddedEvent() {
        viewModel.deleteLastEvent()
        coVerifySequence {
            getLastEventUseCase.execute()
            deleteEventUseCase.execute(startDate = any())
        }

    }

    @Test
    fun whenUpdateEventIsCalledShouldUpdateJustAddedEvent() {
        testIoScope.launch { viewModel.updateEvent("Название для теста") }
        coVerifySequence {
            getLastEventUseCase.execute()
            getEventByIdUseCase.execute(any())
            getAllLocationsByIdUseCase.execute(any())
            getStepCountUseCase.execute(any())
            updateEventUseCase.execute(
                startDate = any(),
                name = any(),
                trackingStateModel = any()
            )
        }
    }
}