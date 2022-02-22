package com.artezio.osport.tracker.presentation.tracker

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artezio.osport.tracker.data.trackservice.ServiceLifecycleState
import com.artezio.osport.tracker.databinding.FragmentTrackerBinding
import com.artezio.osport.tracker.domain.model.Event
import com.artezio.osport.tracker.domain.usecases.GetLastEventIdUseCase
import com.artezio.osport.tracker.domain.usecases.GetLocationsByEventIdUseCase
import com.artezio.osport.tracker.domain.usecases.InsertEventUseCase
import com.artezio.osport.tracker.presentation.TrackService
import com.artezio.osport.tracker.util.START_FOREGROUND_SERVICE
import com.artezio.osport.tracker.util.STOP_FOREGROUND_SERVICE
import com.artezio.osport.tracker.util.millisecondsToDateFormat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val getLastEventIdUseCase: GetLastEventIdUseCase,
    private val insertEventUseCase: InsertEventUseCase,
    private val getLocationsByEventIdUseCase: GetLocationsByEventIdUseCase,
) : ViewModel() {
    val lastEventIdFlow: Flow<Long>
        get() = getLastEventIdUseCase.execute()

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

    fun startService(context: Context, lastEventId: Long) {
        val intent = Intent(context, TrackService::class.java).apply {
            putExtra("eventId", lastEventId)
            action = START_FOREGROUND_SERVICE
        }
        context.startService(intent)
    }

    fun animateCamera(googleMap: GoogleMap, currentLocation: LatLng) {
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                currentLocation,
                23F
            )
        )
    }

    fun stopService(context: Context) {
        val intent = Intent(context, TrackService::class.java).apply {
            action = STOP_FOREGROUND_SERVICE
        }
        context.stopService(intent)
    }

    fun observeServiceState(viewLifecycleOwner: LifecycleOwner, binding: FragmentTrackerBinding) {
        TrackService.serviceLifecycleState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ServiceLifecycleState.Running -> {
                    binding.fabStart.visibility = View.GONE
                    binding.fabStop.visibility = View.VISIBLE
                }
                is ServiceLifecycleState.Stopped -> {
                    binding.fabStop.visibility = View.GONE
                    binding.fabStart.visibility = View.VISIBLE
                }
            }
        }
    }
}