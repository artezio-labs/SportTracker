package com.artezio.osport.tracker.presentation.tracker.shedule

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.artezio.osport.tracker.domain.usecases.DeletePlannedEventUseCase
import com.artezio.osport.tracker.domain.usecases.GetLastEventUseCase
import com.artezio.osport.tracker.domain.usecases.GetLastPlannedEventUseCase
import com.artezio.osport.tracker.presentation.TrackService
import com.artezio.osport.tracker.util.SECOND_IN_MILLIS
import com.artezio.osport.tracker.util.START_PLANNED_SERVICE
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TrackerStartPlanningTrainWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val deletePlannedEventUseCase: DeletePlannedEventUseCase,
    private val getLastPlannedEventUseCase: GetLastPlannedEventUseCase,
    private val getLastEventUseCase: GetLastEventUseCase,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val eventName = inputData.getString(EVENT_NAME) ?: ""
        val plannedEvent = getLastPlannedEventUseCase.execute()
        val timerDelay = inputData.getLong(TIMER_DELAY, 0)
        val calibrationTime = inputData.getLong(CALIBRATION_TIME, 60 * SECOND_IN_MILLIS)
        val lastEventId = getLastEventId()
        deletePlannedEventUseCase.execute(if (plannedEvent != null) plannedEvent.id else 0L)
        val intent = Intent(context, TrackService::class.java).apply {
            action = START_PLANNED_SERVICE
            putExtra(EVENT_ID, lastEventId)
            putExtra(EVENT_NAME, eventName)
            putExtra(TIMER_DELAY, timerDelay)
            putExtra(CALIBRATION_TIME, calibrationTime)
        }
        context.startService(intent)
        return Result.success()
    }

    private suspend fun getLastEventId(): Long {
        val lastEvent = getLastEventUseCase.execute()
        return if (lastEvent != null) {
            lastEvent.id
        } else {
            0L
        }
    }

    companion object {
        private const val EVENT_NAME = "eventName"
        private const val EVENT_ID = "eventId"
        private const val TIMER_DELAY = "timer_delay"
        private const val CALIBRATION_TIME = "calibration_time"
    }
}