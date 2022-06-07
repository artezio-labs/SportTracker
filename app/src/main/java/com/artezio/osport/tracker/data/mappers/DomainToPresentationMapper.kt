package com.artezio.osport.tracker.data.mappers

import com.artezio.osport.tracker.domain.model.EventWithData
import com.artezio.osport.tracker.presentation.main.recycler.Item

class DomainToPresentationMapper : IMapper<EventWithData, Item.Event> {
    override fun map(obj: EventWithData) =
        Item.Event(
            id = obj.event.id,
            eventName = obj.event.name,
            startDate = obj.event.startDate,
            endDate = getEndDate(obj)
        )

    private fun getEndDate(event: EventWithData): Long {
        if(event.locationDataList.isNotEmpty()){
            return event.locationDataList.last().time
        }
        if (event.pedometerDataList.isNotEmpty()){
            return event.pedometerDataList.last().time
        }
        return event.event.startDate
    }
}