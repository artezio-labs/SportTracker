package com.artezio.osport.tracker.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.data.db.EventsDao
import com.artezio.osport.tracker.domain.model.Event
import com.artezio.osport.tracker.domain.model.TrackingStateModel
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventsRepositoryTest {

    private lateinit var repository: EventsRepository
    private lateinit var dao: EventsDao
    private lateinit var testEvent: Event
    private lateinit var trackingStateModel: TrackingStateModel

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        repository = EventsRepository(dao)
        testEvent = mockk(relaxed = true)
        trackingStateModel = mockk(relaxed = true)
    }

    @Test
    fun getAllEvents() = runBlocking {
        repository.getAllEvents()
        coVerify { dao.getAllEventsWithData() }
    }

    @Test
    fun addEvent() = runBlocking {
        repository.addEvent(testEvent)
        coVerify { dao.insertEvent(any()) }
    }

    @Test
    fun getEventById() = runBlocking {
        repository.getEventById(1)
        coVerify { dao.getEventById(1) }
    }

    @Test
    fun updateEventWithTrackingState() = runBlocking {
        repository.updateEvent(0, "Тест", trackingStateModel)
        coVerify {
            dao.updateEvent(
                0,
                "Тест",
                any(),
                trackingStateModel.timerValue,
                trackingStateModel.speedValue,
                trackingStateModel.stepsValue,
                trackingStateModel.gpsPointsValue
            )
        }
    }

    @Test
    fun updateEventWithNameAndStartDate() = runBlocking {
        repository.updateEvent(testEvent.id, testEvent.name, testEvent.startDate)
        coVerify { dao.updateSpecificEventFields(testEvent.id, testEvent.name, testEvent.startDate, null) }
    }

    @Test
    fun getEventWithDataById() = runBlocking {
        repository.getEventWithDataById(1)
        coVerify { dao.getEventWithDataById(1) }
    }

    @Test
    fun getLastEventId() = runBlocking {
        repository.getLastEventId()
        coVerify { dao.getLastEventId() }
    }

    @Test
    fun getLastEvent() = runBlocking {
        repository.getLastEvent()
        coVerify { dao.getLastEvent() }
    }

    @Test
    fun deleteEventByStartDate() = runBlocking {
        repository.deleteEventByStartDate(0)
        coVerify { dao.deleteEventByStartDate(0) }
    }
}