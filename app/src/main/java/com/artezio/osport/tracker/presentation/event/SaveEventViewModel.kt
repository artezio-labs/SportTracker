package com.artezio.osport.tracker.presentation.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artezio.osport.tracker.domain.model.TrackingStateModel
import com.artezio.osport.tracker.domain.usecases.DeleteEventUseCase
import com.artezio.osport.tracker.domain.usecases.GetLastEventIdUseCase
import com.artezio.osport.tracker.domain.usecases.GetLastEventUseCase
import com.artezio.osport.tracker.domain.usecases.UpdateEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveEventViewModel @Inject constructor(
    private val deleteEventUseCase: DeleteEventUseCase,
    private val getLastEventIdUseCase: GetLastEventIdUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val getLastEventUseCase: GetLastEventUseCase
) : ViewModel() {

    val lastEventId: Flow<Long>
        get() = getLastEventIdUseCase.execute()

    fun deleteEvent(eventId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteEventUseCase.execute(eventId)
        }
    }

    fun updateEvent(eventId: Long, eventName: String, trackingStateModel: TrackingStateModel) {
        viewModelScope.launch(Dispatchers.IO) {
            updateEventUseCase.execute(eventId, eventName, trackingStateModel)
        }
    }

    fun updateEvent(eventName: String, trackingStateModel: TrackingStateModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val event = getLastEventUseCase.execute()
            val updatedEvent = event.copy(
                name = eventName,
                endDate = System.currentTimeMillis(),
                timerValue = trackingStateModel.timerValue,
                speedValue = trackingStateModel.speedValue,
                tempoValue = trackingStateModel.tempoValue,
                stepsValue = trackingStateModel.stepsValue,
                gpsPointsValue = trackingStateModel.gpsPointsValue
            )

        }
    }
}