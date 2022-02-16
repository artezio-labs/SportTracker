package com.artezio.sporttracker.data.mappers

import com.artezio.sporttracker.domain.model.EventWithData
import com.artezio.sporttracker.presentation.main.recycler.Item

class DomainToPresentationMapper : IMapper<EventWithData, Item.Event> {
    override fun map(obj: EventWithData) =
        Item.Event(
            id = obj.event.id,
            eventName = obj.event.name,
            startDate = obj.event.startDate,
            endDate = obj.event.endDate
        )
}