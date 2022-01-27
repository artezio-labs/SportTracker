package com.artezio.sporttracker.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artezio.sporttracker.domain.model.Event
import com.artezio.sporttracker.domain.model.EventWithData
import com.artezio.sporttracker.domain.usecases.GetAllEventsWithDataUseCase
import com.artezio.sporttracker.domain.usecases.InsertEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val insertEventUseCase: InsertEventUseCase,
    private val getAllEventsWithDataUseCase: GetAllEventsWithDataUseCase
) : ViewModel() {

    val eventsWithDataFlow: Flow<List<EventWithData>>
        get() = getAllEventsWithDataUseCase.execute()

    fun insertEvent(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        insertEventUseCase.execute(event)
    }

}