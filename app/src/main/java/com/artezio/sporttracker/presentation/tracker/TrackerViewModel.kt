package com.artezio.sporttracker.presentation.tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artezio.sporttracker.domain.model.Event
import com.artezio.sporttracker.domain.usecases.GetAllLocationDataUseCase
import com.artezio.sporttracker.domain.usecases.GetLastEventIdUseCase
import com.artezio.sporttracker.domain.usecases.GetLocationsByEventIdUseCase
import com.artezio.sporttracker.domain.usecases.InsertEventUseCase
import com.artezio.sporttracker.util.millisecondsToDateFormat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val getAllLocationDataUseCase: GetAllLocationDataUseCase,
    private val getLastEventIdUseCase: GetLastEventIdUseCase,
    private val insertEventUseCase: InsertEventUseCase,
    private val getLocationsByEventIdUseCase: GetLocationsByEventIdUseCase
) : ViewModel() {

    val lastEventIdFlow: Flow<Long>
        get() = getLastEventIdUseCase.execute()

    fun insertEventUseCase(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        insertEventUseCase.execute(event)
    }

    fun buildRoute(locations: List<LatLng>, googleMap: GoogleMap) {
        var lineOptions = PolylineOptions()
        lineOptions = lineOptions.addAll(locations)
        googleMap.addPolyline(lineOptions)
    }

    fun getLocationsByEventId(id: Long) = getLocationsByEventIdUseCase.execute(id)

    fun generateEvent() = viewModelScope.launch(Dispatchers.IO) {
        val currentTime = System.currentTimeMillis()
        val event = Event(
            name = "Ивент от ${millisecondsToDateFormat(currentTime)}",
            startDate = currentTime,
            endDate = null,
            sportsmanId = 0
        )
        insertEventUseCase.execute(event)
    }

}