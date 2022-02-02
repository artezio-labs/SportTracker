package com.artezio.sporttracker.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artezio.sporttracker.domain.model.Event
import com.artezio.sporttracker.domain.model.EventWithData
import com.artezio.sporttracker.domain.usecases.GetAllEventsWithDataUseCase
import com.artezio.sporttracker.domain.usecases.GetEventByIdUseCase
import com.artezio.sporttracker.domain.usecases.InsertEventUseCase
import com.artezio.sporttracker.presentation.main.recycler.Item
import com.artezio.sporttracker.util.mapDomainEventModelToPresentationEventModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val insertEventUseCase: InsertEventUseCase,
    private val getAllEventsWithDataUseCase: GetAllEventsWithDataUseCase,
    private val getEventByIdUseCase: GetEventByIdUseCase
) : ViewModel() {

    val eventsWithDataFlow: Flow<List<EventWithData>>
        get() = getAllEventsWithDataUseCase.execute()

    fun insertEvent(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        insertEventUseCase.execute(event)
    }

    fun getEventById(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        getEventByIdUseCase.execute(id)
    }

    fun buildListOfEvents(list: List<EventWithData>): List<Item> {

        return list.map { mapDomainEventModelToPresentationEventModel(it) }
    }

}