package com.artezio.osport.tracker.data.db

import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.artezio.osport.tracker.domain.model.Event
import junit.framework.Assert.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventsDaoTest {

    private lateinit var db: TrackerDb
    private lateinit var dao: EventsDao
    private lateinit var testEvent: Event

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            TrackerDb::class.java
        ).build()
        dao = db.eventsDao()
        testEvent = Event("Тестовый ивент", 0, 0, 0.0)
    }

    @Test
    fun insertEvents() = runBlocking {
        dao.insertEvent(testEvent)
        assert(dao.getAllEvents().isNotEmpty())
    }

    @Test
    fun getEventById() = runBlocking {
        dao.insertEvent(testEvent)
        assertNotNull(dao.getEventById(1))
    }

    @Test
    fun getAllEvents() = runBlocking {
        for (i in 0..5) {
            dao.insertEvent(testEvent.copy(name = "Тренировка №$i"))
        }
        val allEvents = dao.getAllEvents()
        Log.d("dao_test", "getAllEvents: ${allEvents.size}")
        assert(allEvents.size == 6)
    }

    @Test
    fun updateEventBySpecificFields() = runBlocking {
        dao.insertEvent(testEvent)
        val eventToUpdate =
            testEvent.copy(name = "Обновленный ивент", startDate = 342342304, 0, 0.0)
        dao.updateSpecificEventFields(
            1,
            eventToUpdate.name,
            eventToUpdate.startDate,
        )
        assert(dao.getEventById(1) == eventToUpdate)
    }

    @Test
    fun updateEventWithTrackingState() = runBlocking {
        dao.insertEvent(testEvent)
        val eventToUpdate = Event(
            name = "Обновленный ивент",
            startDate = 0,
            sportsmanId = 0,
            timerValue = 0.0
        )
        dao.updateEvent(
            startDate = eventToUpdate.startDate,
            eventName = eventToUpdate.name,
            timerValue = eventToUpdate.timerValue,
        )
        assert(dao.getEventById(1) == eventToUpdate)
    }

    @Test
    fun getAllEventsWithDataFlow(): Unit = runBlocking {
        val testListOfEvents = mutableListOf<Event>()
        for (i in 0..5) {
            testListOfEvents.add(testEvent.copy(name = "Тренировка №$i"))
            dao.insertEvent(testListOfEvents[i])
        }
        dao.getAllEventsWithData().map {
            testListOfEvents.forEachIndexed { index, event ->
                assertEquals(event, it[index])
            }
        }
    }

    @Test
    fun getEventWithDataByIdFlow(): Unit = runBlocking {
        dao.insertEvent(testEvent)
        val eventWithData = dao.getEventWithDataById(1)
        eventWithData.map {
            assertEquals(it.event, testEvent)
        }
    }

    @Test
    fun getLastEventIdFlow(): Unit = runBlocking {
        val testListOfEvents = mutableListOf<Event>()
        for (i in 0..5) {
            testListOfEvents.add(testEvent.copy(name = "Тренировка №$i"))
            dao.insertEvent(testListOfEvents[i])
        }
        dao.getLastEventId().map {
            assertEquals(it, testListOfEvents[5].id)
        }
    }

    @Test
    fun deleteEventByStartDate() = runBlocking {
        dao.insertEvent(testEvent)
        dao.deleteEventByStartDate(testEvent.startDate)
        assertTrue(dao.getAllEvents().isEmpty())
    }

    @Test
    fun getLastEvent() = runBlocking {
        val testListOfEvents = mutableListOf<Event>()
        for (i in 0..5) {
            testListOfEvents.add(testEvent.copy(name = "Тренировка №$i"))
            dao.insertEvent(testListOfEvents[i])
        }
        val lastEvent = dao.getLastEvent()
        assertEquals(lastEvent, testListOfEvents[testListOfEvents.size - 1])
    }

    @After
    fun tearDown() {
        db.close()
    }

}