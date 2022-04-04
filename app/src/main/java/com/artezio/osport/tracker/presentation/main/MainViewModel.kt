package com.artezio.osport.tracker.presentation.main

import androidx.lifecycle.ViewModel
import com.artezio.osport.tracker.data.mappers.DomainToPresentationMapper
import com.artezio.osport.tracker.domain.model.EventWithData
import com.artezio.osport.tracker.domain.usecases.GetAllEventsWithDataUseCase
import com.artezio.osport.tracker.presentation.main.recycler.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllEventsWithDataUseCase: GetAllEventsWithDataUseCase,
    private val domainToPresentationMapper: DomainToPresentationMapper,
) : ViewModel() {
    val eventsWithDataFlow: Flow<List<EventWithData>>
        get() = getAllEventsWithDataUseCase.execute()
            .map { list ->
                list.filter { it.event.endDate != null }
            }

    fun buildListOfEvents(list: List<EventWithData>): List<Item> =
        list.map { domainToPresentationMapper.map(it) }
            .sortedByDescending { it.id }
}