package com.artezio.osport.tracker.presentation.tracker

import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.mappers.LocationToPointMapper
import com.artezio.osport.tracker.data.trackservice.ServiceLifecycleState
import com.artezio.osport.tracker.databinding.FragmentSessionRecordingBinding
import com.artezio.osport.tracker.databinding.FragmentTrackerStatisticsBinding
import com.artezio.osport.tracker.domain.model.Event
import com.artezio.osport.tracker.domain.model.LocationPointData
import com.artezio.osport.tracker.domain.model.PedometerData
import com.artezio.osport.tracker.domain.usecases.*
import com.artezio.osport.tracker.presentation.BaseViewModel
import com.artezio.osport.tracker.presentation.TrackService
import com.artezio.osport.tracker.util.*
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val getLastEventIdUseCase: GetLastEventIdUseCase,
    private val insertEventUseCase: InsertEventUseCase,
    private val getLocationsByEventIdUseCase: GetLocationsByEventIdUseCase,
    private val getAllLocationsByIdUseCase: GetAllLocationsByIdUseCase,
    private val getAllEventsListUseCase: GetAllEventsListUseCase,
    private val getDataForCadenceUseCase: GetDataForCadenceUseCase,
    private val getLastEventUseCase: GetLastEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val accuracyFactory: AccuracyFactory,
    private val mapper: LocationToPointMapper
) : BaseViewModel() {
    val lastEventIdFlow: Flow<Long>
        get() = getLastEventIdUseCase.execute()

    val pedometerDataForCadence: Flow<List<PedometerData>>
        get() = getDataForCadenceUseCase.execute()

    val timerValueLiveData: LiveData<Double>
        get() = TrackService.timerValueLiveData

    val stepsLiveData: LiveData<Int>
        get() = TrackService.stepsLiveData

    val locations: MutableLiveData<List<Point>> = MutableLiveData()

    val currentFragmentIdLiveData = MutableLiveData(R.id.trackerFragment3)

    fun getLocationsByEventIdWithAccuracy(id: Long) =
        getLocationsByEventIdUseCase.executeWithAccuracy(id)

    suspend fun getLocationsByEventId(): Flow<List<Point>> {
        val id = getLastEventId()
        return id?.let { getLocationsByEventIdUseCase.execute(it) } ?: flowOf(emptyList())
    }

    suspend fun getLastEventId(): Long? = viewModelScope.async(Dispatchers.IO) {
        return@async getLastEventUseCase.execute()?.id
    }.await()

    fun deleteLastEvent() = viewModelScope.launch(Dispatchers.IO) {
        val lastEvent = getLastEventUseCase.execute()
        deleteEventUseCase.execute(lastEvent.startDate)
    }

    fun generateEvent() = viewModelScope.launch(Dispatchers.IO) {
        val currentTime = System.currentTimeMillis()
        val newEventName = formatEventName(currentTime)
        val events = getAllEventsListUseCase.execute()
        val event = Event(
            name = buildEventName(newEventName, events),
            startDate = currentTime,
            sportsmanId = 0
        )
        Log.d("event_save", "Saved event: $event")
        insertEventUseCase.execute(event)
    }

    fun startService(context: Context, lastEventId: Long) {
        val intent = Intent(context, TrackService::class.java).apply {
            putExtra("eventId", lastEventId)
            action = START_FOREGROUND_SERVICE
        }
        context.startService(intent)
    }

    fun calculateAccuracy(location: Location): Pair<String, IAccuracyFactory.AccuracyType> {
        val accuracy = accuracyFactory.calculateAccuracy(location.accuracy)
        val accuracyString = String.format("%.2f м.", location.accuracy)
        return Pair(accuracyString, accuracy)
    }

    fun stopService(context: Context) {
        val intent = Intent(context, TrackService::class.java).apply {
            action = STOP_FOREGROUND_SERVICE
        }
        context.stopService(intent)
        TrackService.stepsLiveData.value = 0
    }

    fun observeServiceState(
        viewLifecycleOwner: LifecycleOwner,
        binding: FragmentSessionRecordingBinding
    ) {
        TrackService.serviceLifecycleState.observe(viewLifecycleOwner) { state ->
            when (state) {
                ServiceLifecycleState.RUNNING -> {
                    binding.fabStopPause.visibility = View.VISIBLE
                    binding.fabToSessionMap.visibility = View.VISIBLE
                    binding.llAccuracy.visibility = View.GONE
                    Log.d("service_state", "RUNNING")
                }
                ServiceLifecycleState.STOPPED -> {
                    binding.llFabs.visibility = View.GONE
                    binding.fabStart.visibility = View.VISIBLE
                    binding.fabToSessionStatistics.visibility = View.GONE
                    binding.fabToSessionMap.visibility = View.GONE
                    binding.llAccuracy.visibility = View.GONE
                    Log.d("service_state", "STOPPED")
                }
                ServiceLifecycleState.PAUSED -> {
                    binding.fabStopPause.visibility = View.GONE
                    binding.llFabs.visibility = View.VISIBLE
                    binding.fabStart.visibility = View.GONE
                    binding.llAccuracy.visibility = View.GONE
                    Log.d("service_state", "PAUSED")
                }
                ServiceLifecycleState.RESUMED -> {
                    binding.llFabs.visibility = View.GONE
                    binding.fabStopPause.visibility = View.VISIBLE
                    binding.llAccuracy.visibility = View.GONE
                    Log.d("service_state", "RESUMED")
                }
                ServiceLifecycleState.NOT_STARTED -> {
                    binding.fabToSessionStatistics.visibility = View.GONE
                    binding.fabToSessionMap.visibility = View.GONE
                    binding.llAccuracy.visibility = View.VISIBLE
                }
            }
        }
    }

    fun observeServiceStateWhenPaused(
        viewLifecycleOwner: LifecycleOwner,
        binding: FragmentTrackerStatisticsBinding
    ) {
        TrackService.serviceLifecycleState.observe(viewLifecycleOwner) { state ->
            when (state) {
                ServiceLifecycleState.PAUSED -> {
                    binding.statisticsPaused.visibility = View.VISIBLE
                    binding.statisticsNotPaused.visibility = View.GONE
                }
                else -> {
                    binding.statisticsPaused.visibility = View.GONE
                    binding.statisticsNotPaused.visibility = View.VISIBLE
                }
            }
        }
    }

    fun calculateDistance(locations: List<Pair<LocationPointData, IAccuracyFactory.AccuracyType>>): Double {
        var totalDistance = 0.0
        if (locations.size <= 1) return totalDistance
        val data = locations.map { it.first }
        for (i in 0 until data.size - 1) {
            totalDistance += distanceBetween(data[i], data[i + 1])
        }
        return totalDistance / 1000
    }

    fun pauseTracking(context: Context) {
        val intent = Intent(context, TrackService::class.java).apply {
            action = PAUSE_FOREGROUND_SERVICE
        }
        context.startService(intent)
    }

    fun resumeTracking(context: Context) {
        val intent = Intent(context, TrackService::class.java).apply {
            action = RESUME_FOREGROUND_SERVICE
        }
        context.startService(intent)
    }

    fun navigateToSaveEventFragment(eventId: Long) {
        navigate(
            SessionRecordingFragmentDirections.actionSessionRecordingFragmentToSaveEventFragment2(
                eventId
            )
        )
    }

    private fun buildEventName(newEventName: String, events: List<String>): String {
        return try {
            val lastSameElement = events.first { event -> event.startsWith(newEventName) }
            Log.d("event_name", "Last event same name: $lastSameElement")
            val lastSameElementNumber = lastSameElement.split("_")
            if (lastSameElementNumber.size == 1) {
                newEventName + "_{1}"
            } else {
                Log.d("event_name", "Last event same name split: $lastSameElementNumber")
                "${newEventName}_{${
                    lastSameElementNumber[1].replace("\\D".toRegex(), "").toInt() + 1
                }}"
            }

        } catch (ex: Exception) {
            newEventName
        }
    }

    fun calculateCadence(data: List<PedometerData>): Int {
        if (data.isEmpty()) return 0
        return data.last().stepCount - data.first().stepCount
    }
}