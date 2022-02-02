package com.artezio.sporttracker.presentation.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artezio.sporttracker.domain.model.Event
import com.artezio.sporttracker.domain.usecases.GetEventByIdUseCase
import com.artezio.sporttracker.domain.usecases.InsertEventUseCase
import com.artezio.sporttracker.domain.usecases.UpdateEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EventViewModel @Inject constructor(
    private val getEventByIdUseCase: GetEventByIdUseCase,
    private val insertEventUseCase: InsertEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase
) : ViewModel() {

    fun saveOrUpdateEvent(id: Long, name: String, startDate: Long) = viewModelScope.launch(Dispatchers.IO) {
        if(id == -1L) {
            insertEventUseCase.execute(Event(
                name,
                startDate,
                null,
                0
            ))
        } else {
            updateEventUseCase.execute(id, name, startDate)
        }
    }

}