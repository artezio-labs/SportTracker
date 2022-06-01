package com.artezio.osport.tracker.presentation.main

import com.artezio.osport.tracker.data.mappers.DomainToPresentationMapper
import com.artezio.osport.tracker.data.trackservice.ServiceLifecycleState
import com.artezio.osport.tracker.domain.model.EventWithData
import com.artezio.osport.tracker.domain.usecases.GetAllEventsWithDataUseCase
import com.artezio.osport.tracker.presentation.BaseViewModel
import com.artezio.osport.tracker.presentation.TrackService
import com.artezio.osport.tracker.presentation.main.recycler.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllEventsWithDataUseCase: GetAllEventsWithDataUseCase,
    private val domainToPresentationMapper: DomainToPresentationMapper
) : BaseViewModel() {
    val eventsWithDataFlow: Flow<List<EventWithData>>
        get() = if (TrackService.serviceLifecycleState.value == ServiceLifecycleState.RUNNING
            || TrackService.serviceLifecycleState.value == ServiceLifecycleState.PAUSED
        ) {
            getAllEventsWithDataUseCase.execute().map { it.dropLast(1) }
        } else {
            getAllEventsWithDataUseCase.execute()
        }

    fun buildListOfEvents(list: List<EventWithData>): List<Item> =
        list.map { domainToPresentationMapper.map(it) }
            .sortedByDescending { it.id }
}