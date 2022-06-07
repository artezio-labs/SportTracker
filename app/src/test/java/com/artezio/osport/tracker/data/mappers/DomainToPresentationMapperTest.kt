package com.artezio.osport.tracker.data.mappers

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.domain.model.Event
import com.artezio.osport.tracker.domain.model.EventWithData
import com.artezio.osport.tracker.presentation.main.recycler.Item
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DomainToPresentationMapperTest {

    @Test
    fun `test domain to presentation object mapper`() {
        val mapper = DomainToPresentationMapper()
        assertEquals(
            Item.Event(0L, "Тест", 1000L, 1000L),
            mapper.map(
                EventWithData(Event("Тест", 1000L, 0L, 0.0), emptyList(), emptyList(), emptyList())
            )
        )
    }
}