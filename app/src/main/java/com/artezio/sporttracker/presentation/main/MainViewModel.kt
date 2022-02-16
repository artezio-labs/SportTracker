package com.artezio.sporttracker.presentation.main

import androidx.lifecycle.ViewModel
import com.artezio.sporttracker.data.mappers.DomainToPresentationMapper
import com.artezio.sporttracker.domain.model.EventWithData
import com.artezio.sporttracker.domain.usecases.GetAllEventsWithDataUseCase
import com.artezio.sporttracker.presentation.main.recycler.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllEventsWithDataUseCase: GetAllEventsWithDataUseCase,
    private val domainToPresentationMapper: DomainToPresentationMapper
) : ViewModel() {
    val eventsWithDataFlow: Flow<List<EventWithData>>
        get() = getAllEventsWithDataUseCase.execute()

    fun buildListOfEvents(list: List<EventWithData>): List<Item> =
        list.map { domainToPresentationMapper.map(it) }
            .sortedByDescending { it.id }
}