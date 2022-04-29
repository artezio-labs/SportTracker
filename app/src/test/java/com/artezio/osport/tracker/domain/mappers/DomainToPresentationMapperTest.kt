package com.artezio.osport.tracker.domain.mappers

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.data.mappers.DomainToPresentationMapper
import com.artezio.osport.tracker.domain.model.Event
import com.artezio.osport.tracker.domain.model.EventWithData
import com.artezio.osport.tracker.presentation.main.recycler.Item
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DomainToPresentationMapperTest {

    @Test
    fun map() {
        val mapper = DomainToPresentationMapper()
        val itemToMap = EventWithData(
            event = Event("Тестовый ивент", 0, 0, 0, 0.0, 0.0, 0, 0),
            pedometerDataList = mockk(),
            locationDataList = mockk(),
            trackDataList = mockk()
        )
        val mappedItem = mapper.map(itemToMap)
        assertEquals(
            mappedItem,
            Item.Event(
                itemToMap.event.id,
                itemToMap.event.name,
                itemToMap.event.startDate,
                itemToMap.event.endDate
            )
        )
    }
}