package com.artezio.osport.tracker.presentation.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.mappers.DomainPlannedEventsToPresentationMapper
import com.artezio.osport.tracker.data.mappers.DomainToPresentationMapper
import com.artezio.osport.tracker.data.trackservice.ServiceLifecycleState
import com.artezio.osport.tracker.domain.model.EventWithData
import com.artezio.osport.tracker.domain.usecases.DeletePlannedEventUseCase
import com.artezio.osport.tracker.domain.usecases.GetAllEventsWithDataUseCase
import com.artezio.osport.tracker.domain.usecases.GetAllPlannedEventsUseCase
import com.artezio.osport.tracker.domain.usecases.GetLastPlannedEventUseCase
import com.artezio.osport.tracker.presentation.BaseViewModel
import com.artezio.osport.tracker.presentation.TrackService
import com.artezio.osport.tracker.presentation.main.recycler.Item
import com.artezio.osport.tracker.util.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllEventsWithDataUseCase: GetAllEventsWithDataUseCase,
    private val deletePlannedEventUseCase: DeletePlannedEventUseCase,
    private val getAllPlannedEventsUseCase: GetAllPlannedEventsUseCase,
    private val getLastPlannedEventUseCase: GetLastPlannedEventUseCase,
    private val domainToPresentationMapper: DomainToPresentationMapper,
    private val domainPlannedEventsToPresentationMapper: DomainPlannedEventsToPresentationMapper,
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

    val plannedEventsFlow: Flow<List<Item.PlannedEvent>>
        get() = getAllPlannedEventsUseCase.executeWithFlow().map { events ->
            events.map { domainPlannedEventsToPresentationMapper.map(it) }.sortedBy { it.startDate }
        }

    val lastPlannedEventIdLiveData = MutableLiveData(0L)

    fun buildListOfEvents(list: List<EventWithData>): List<Item> =
        list.map { domainToPresentationMapper.map(it) }
            .sortedByDescending { it.id }

    fun getTabsTitles(): Array<String> = resourceProvider.getStringArray(R.array.tabs_titles)

    fun getLastPlannedEventId() = viewModelScope.launch(Dispatchers.IO) {
        val lastPlannedEvent = getLastPlannedEventUseCase.execute()
        val id = if (lastPlannedEvent == null) 0L else lastPlannedEvent.id
        lastPlannedEventIdLiveData.postValue(id)
    }

    fun deletePlannedEvent(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        deletePlannedEventUseCase.execute(id)
    }
}