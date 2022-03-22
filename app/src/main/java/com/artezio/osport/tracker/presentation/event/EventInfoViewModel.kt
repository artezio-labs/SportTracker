package com.artezio.osport.tracker.presentation.event

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artezio.osport.tracker.domain.model.Event
import com.artezio.osport.tracker.domain.model.LocationPointData
import com.artezio.osport.tracker.domain.usecases.GetAllLocationsByIdUseCase
import com.artezio.osport.tracker.domain.usecases.GetEventByIdUseCase
import com.artezio.osport.tracker.util.distanceBetween
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventInfoViewModel @Inject constructor(
    private val getEventByIdUseCase: GetEventByIdUseCase,
    private val getAllLocationsByIdUseCase: GetAllLocationsByIdUseCase
) : ViewModel() {
    val eventLiveData: MutableLiveData<Event> = MutableLiveData()

    fun getEventById(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        eventLiveData.postValue(getEventByIdUseCase.execute(id))
    }

    fun getDistanceByEventId(id: Long): Pair<Double, Int> {
        var locationsPointData: List<LocationPointData> = emptyList()
        viewModelScope.launch(Dispatchers.IO) {
            locationsPointData = getAllLocationsByIdUseCase.execute(id)
        }
        return Pair(calculateDistance(locationsPointData), locationsPointData.size)
    }

    fun formatTime(time: Double): String {
        val sb = StringBuilder()
        val hours = time / 3600
        val minutes = (time % 3600) / 60
        val seconds = time % 60
        if (hours >= 1)
            sb.append("$hours ч. ")
        if (minutes >= 1)
            sb.append("$minutes мин. ")
        if (seconds != 0.0)
            sb.append("$seconds сек.")
        return sb.toString()
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