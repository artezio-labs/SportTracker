package com.artezio.osport.tracker.presentation.event

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artezio.osport.tracker.domain.model.EventInfo
import com.artezio.osport.tracker.domain.usecases.GetAllLocationsByIdUseCase
import com.artezio.osport.tracker.domain.usecases.GetEventInfoUseCase
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventInfoViewModel @Inject constructor(
    private val getEventInfoUseCase: GetEventInfoUseCase,
    private val getAllLocationsByIdUseCase: GetAllLocationsByIdUseCase
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

}