package com.artezio.osport.tracker.presentation.event

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artezio.osport.tracker.domain.model.LocationPointData
import com.artezio.osport.tracker.domain.model.PedometerData
import com.artezio.osport.tracker.domain.model.TrackingStateModel
import com.artezio.osport.tracker.domain.usecases.*
import com.artezio.osport.tracker.util.distanceBetween
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class SaveEventViewModel @Inject constructor(
    private val deleteEventUseCase: DeleteEventUseCase,
    private val getLastEventUseCase: GetLastEventUseCase,
    private val getEventByIdUseCase: GetEventByIdUseCase,
    private val getAllLocationsByIdUseCase: GetAllLocationsByIdUseCase,
    private val getStepCountUseCase: GetStepCountUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
) : ViewModel() {

    fun deleteLastEvent() = viewModelScope.launch(Dispatchers.IO) {
        val lastEvent = getLastEventUseCase.execute()
        Log.d("event_delete", "Last event: $lastEvent")
        deleteEventUseCase.execute(lastEvent.startDate)
    }

    fun updateEvent(eventName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val lastEvent = getLastEventUseCase.execute()
            Log.d("event_update", "Last event from db to update: $lastEvent")
            update(lastEvent.id, eventName)
        }
    }

    private suspend fun update(eventId: Long, eventName: String) {
        val event = getEventByIdUseCase.execute(eventId)
        Log.d("event_update", "Event from db to update: $event")
        val locations = getAllLocationsByIdUseCase.execute(eventId)
        val steps = getStepCountUseCase.execute(eventId)
        Log.d("event_save", "Locations: $locations")

        val trackingState = buildTrackingState(eventId, locations, steps)
        Log.d("event_save", "TrackerState before saving: $trackingState")
        if (eventName.isEmpty()) {
            updateEventUseCase.execute(event.startDate, event.name, trackingState)
        } else {
            updateEventUseCase.execute(event.startDate, eventName, trackingState)
        }
    }

    private fun buildTrackingState(
        eventId: Long,
        locations: List<LocationPointData>,
        steps: PedometerData
    ): TrackingStateModel {
        val time = if (locations.isEmpty() || locations.size == 1) {
            0
        } else {
            (locations.last().time - locations.first().time) / 1000
        }
        val speedValue = locations.map { it.speed * 3.6 }.average()
        val distanceValue = calculateDistance(locations)
        val tempoValue =
            if (distanceValue != 0.0) (time / 60.0 + (time % 60.0) / 60.0) / distanceValue else 0.0
        val stepsValue = if (steps == null) 0 else steps.stepCount
        val gpsPointsValue = locations.size

        return TrackingStateModel(
            timerValue = time.toDouble(),
            speedValue = speedValue,
            distanceValue = distanceValue,
            tempoValue = tempoValue,
            stepsValue = stepsValue,
            gpsPointsValue = gpsPointsValue,
            eventId = eventId
        )
    }

    private fun calculateDistance(locations: List<LocationPointData>): Double {
        var totalDistance = 0.0
        if (locations.size <= 1) return totalDistance
        for (i in 0 until locations.size - 1) {
            totalDistance += distanceBetween(locations[i], locations[i + 1])
        }
        return totalDistance / 1000
    }
}