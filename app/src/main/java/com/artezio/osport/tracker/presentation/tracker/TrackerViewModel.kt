package com.artezio.osport.tracker.presentation.tracker

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.*
import com.artezio.osport.tracker.data.trackservice.ServiceLifecycleState
import com.artezio.osport.tracker.databinding.FragmentTrackerBinding
import com.artezio.osport.tracker.domain.model.Event
import com.artezio.osport.tracker.domain.model.LocationPointData
import com.artezio.osport.tracker.domain.model.TrackingStateModel
import com.artezio.osport.tracker.domain.usecases.*
import com.artezio.osport.tracker.presentation.TrackService
import com.artezio.osport.tracker.util.START_FOREGROUND_SERVICE
import com.artezio.osport.tracker.util.STOP_FOREGROUND_SERVICE
import com.artezio.osport.tracker.util.distanceBetween
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
import kotlin.math.roundToInt

@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val getLastEventIdUseCase: GetLastEventIdUseCase,
    private val insertEventUseCase: InsertEventUseCase,
    private val getLocationsByEventIdUseCase: GetLocationsByEventIdUseCase,
    private val getTrackingStateUseCase: GetTrackingStateUseCase,
    private val saveTrackingStateUseCase: SaveTrackingStateUseCase,
) : ViewModel() {
    val lastEventIdFlow: Flow<Long>
        get() = getLastEventIdUseCase.execute()

    val timerValueLiveData: LiveData<Double>
        get() = TrackService.timerValueLiveData

    val trackingStateLiveData = MutableLiveData(TrackingStateModel.empty())

    fun buildRoute(locations: List<Pair<LocationPointData, Accuracy>>, googleMap: GoogleMap) {
        var lineOptions = PolylineOptions()
        lineOptions =
            lineOptions.addAll(locations.map { LatLng(it.first.latitude, it.first.longitude) })
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
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                currentLocation,
                17F
            )
        )
    }

    fun stopService(context: Context) {
        val intent = Intent(context, TrackService::class.java).apply {
            action = STOP_FOREGROUND_SERVICE
        }
        context.stopService(intent)
    }

    fun observeServiceStateInTrackerFragment(
        viewLifecycleOwner: LifecycleOwner,
        binding: FragmentTrackerBinding
    ) {
        TrackService.serviceLifecycleState.observe(viewLifecycleOwner) { state ->
            when (state) {
                ServiceLifecycleState.RUNNING -> {
                    binding.fabStart.visibility = View.GONE
                    binding.fabStop.visibility = View.VISIBLE
                    binding.fabToTrackerStatistics.visibility = View.VISIBLE
                }
                ServiceLifecycleState.STOPPED -> {
                    binding.fabStop.visibility = View.GONE
                    binding.fabStart.visibility = View.VISIBLE
                    binding.fabToTrackerStatistics.visibility = View.GONE
                }
                ServiceLifecycleState.PAUSED -> {}
                ServiceLifecycleState.RESUMED -> {}
            }
        }
    }

    fun getTimerStringFromDouble(time: Double): String {
        val timeInt = time.roundToInt()
        val hours = timeInt % 86400 / 3600
        val minutes = timeInt % 86400 % 3600 / 60
        val seconds = timeInt % 86400 % 3600 % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun calculateDistance(locations: List<Pair<LocationPointData, Accuracy>>): Double {
        var totalDistance = 0.0
        if (locations.size <= 1) return totalDistance
        val data = locations.map { it.first }
        for (i in 0 until data.size - 1) {
            totalDistance += distanceBetween(data[i], data[i + 1])
        }
        return totalDistance / 1000
    }

    fun saveTrackingState(state: TrackingStateModel) = viewModelScope.launch(Dispatchers.IO) {
        saveTrackingStateUseCase.execute(state)
    }

    fun getTrackingState(): TrackingStateModel? {
        var trackingState: TrackingStateModel? = null
        viewModelScope.launch(Dispatchers.IO) {
            trackingState = getTrackingStateUseCase.execute()
        }
        return trackingState
    }

    fun detectAccuracy(accuracy: Float): Pair<Float, Accuracy> =
        when {
            (0F..5F).contains(accuracy) -> Pair(accuracy, Accuracy.GOOD)
            (5F..15F).contains(accuracy) -> Pair(accuracy, Accuracy.MEDIUM)
            else -> {
                Pair(accuracy, Accuracy.BAD)
            }
        }

}