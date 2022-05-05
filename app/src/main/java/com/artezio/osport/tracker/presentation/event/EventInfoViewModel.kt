package com.artezio.osport.tracker.presentation.event

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artezio.osport.tracker.domain.model.EventInfo
import com.artezio.osport.tracker.domain.usecases.*
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventInfoViewModel @Inject constructor(
    private val getEventInfoUseCase: GetEventInfoUseCase,
    private val getAllLocationsByIdUseCase: GetAllLocationsByIdUseCase,
    private val updateEventNameUseCase: UpdateEventNameUseCase,
    private val getEventByIdUseCase: GetEventByIdUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
) : ViewModel() {
    val eventInfoLiveData = MutableLiveData<EventInfo>()
    val locationsLiveData = MutableLiveData<List<Point>>()

    fun getEventInfo(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val eventInfo = getEventInfoUseCase.execute(id)
        eventInfoLiveData.postValue(eventInfo)
    }

    fun getLocationsById(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val locations = getAllLocationsByIdUseCase.execute(id)
            .map { Point.fromLngLat(it.longitude, it.latitude, it.altitude) }
        locationsLiveData.postValue(locations)
    }

    fun updateEventName(id: Long, name: String) = viewModelScope.launch(Dispatchers.IO) {
        val event = getEventByIdUseCase.execute(id)
        updateEventNameUseCase.execute(name, event.startDate)
    }

    fun deleteEvent(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val event = getEventByIdUseCase.execute(id)
        deleteEventUseCase.execute(event.startDate)
    }
}