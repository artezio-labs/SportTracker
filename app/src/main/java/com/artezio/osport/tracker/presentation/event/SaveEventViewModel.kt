package com.artezio.osport.tracker.presentation.event

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artezio.osport.tracker.data.prefs.PrefsManager
import com.artezio.osport.tracker.domain.usecases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveEventViewModel @Inject constructor(
    private val deleteEventUseCase: DeleteEventUseCase,
    private val getLastEventIdUseCase: GetLastEventIdUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val getLastEventUseCase: GetLastEventUseCase,
    private val insertEvent: InsertEventUseCase,
    private val prefsManager: PrefsManager
) : ViewModel() {

    fun deleteLastEvent() = viewModelScope.launch(Dispatchers.IO) {
        val lastEvent = getLastEventUseCase.execute()
        viewModelScope.launch(Dispatchers.IO) {
            deleteEventUseCase.execute(lastEvent)
        }
    }

    fun updateEvent(eventName: String) {
        val trackingStateModel = prefsManager.trackingState
        Log.d("event_save", "state: $trackingStateModel")
        viewModelScope.launch(Dispatchers.IO) {
            val event = getLastEventUseCase.execute()
            val updatedEvent = event.copy(
                name = eventName,
                endDate = System.currentTimeMillis(),
                timerValue = trackingStateModel.timerValue,
                speedValue = trackingStateModel.speedValue,
                stepsValue = trackingStateModel.stepsValue,
                gpsPointsValue = trackingStateModel.gpsPointsValue
            )
            deleteLastEvent()
            insertEvent.execute(updatedEvent)
        }
    }
}