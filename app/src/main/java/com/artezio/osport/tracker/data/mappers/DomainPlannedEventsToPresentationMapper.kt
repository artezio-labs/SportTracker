package com.artezio.osport.tracker.data.mappers

import com.artezio.osport.tracker.domain.model.PlannedEvent
import com.artezio.osport.tracker.presentation.main.recycler.Item

class DomainPlannedEventsToPresentationMapper : IMapper<PlannedEvent, Item.PlannedEvent> {
    override fun map(obj: PlannedEvent): Item.PlannedEvent {
        return Item.PlannedEvent(
            id = obj.id,
            eventName = obj.name,
            startDate = obj.startDate,
            duration = obj.duration,
            calibrationTime = obj.calibrationTime,
        )
    }

}