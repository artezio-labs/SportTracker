package com.artezio.osport.tracker.presentation.main

import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.mappers.DomainToPresentationMapper
import com.artezio.osport.tracker.data.trackservice.ServiceLifecycleState
import com.artezio.osport.tracker.domain.model.EventWithData
import com.artezio.osport.tracker.domain.model.PlannedEvent
import com.artezio.osport.tracker.domain.usecases.GetAllEventsWithDataUseCase
import com.artezio.osport.tracker.presentation.BaseViewModel
import com.artezio.osport.tracker.presentation.TrackService
import com.artezio.osport.tracker.presentation.main.recycler.Item
import com.artezio.osport.tracker.util.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllEventsWithDataUseCase: GetAllEventsWithDataUseCase,
    private val domainToPresentationMapper: DomainToPresentationMapper,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel() {
    val eventsWithDataFlow: Flow<List<Item.Event>>
        get() {
            val events = getAllEventsWithDataUseCase.execute().map { events ->
                events.map { domainToPresentationMapper.map(it) }
            }
            return if ((TrackService.serviceLifecycleState.value == ServiceLifecycleState.RUNNING
                        || TrackService.serviceLifecycleState.value == ServiceLifecycleState.PAUSED)
            ) {
                events.map { it.dropLast(1) }
            } else {
                events
            }
        }

    private val plannedEventsFlow: Flow<List<PlannedEvent>>
        get() = flowOf()

    fun getEventsByType(isPlanned: Boolean): Flow<List<PlannedEvent>> {
        return plannedEventsFlow
    }

    fun buildListOfEvents(list: List<EventWithData>): List<Item> =
        list.map { domainToPresentationMapper.map(it) }
            .sortedByDescending { it.id }

    fun getTabsTitles(): Array<String> = resourceProvider.getStringArray(R.array.tabs_titles)
}