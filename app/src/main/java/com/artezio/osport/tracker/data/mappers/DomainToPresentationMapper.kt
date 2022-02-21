package com.artezio.osport.tracker.data.mappers

import com.artezio.osport.tracker.domain.model.EventWithData
import com.artezio.osport.tracker.presentation.main.recycler.Item

class DomainToPresentationMapper : IMapper<EventWithData, Item.Event> {
    override fun map(obj: EventWithData) =
        Item.Event(
            id = obj.event.id,
            eventName = obj.event.name,
            startDate = obj.event.startDate,
            endDate = obj.event.endDate
        )
}