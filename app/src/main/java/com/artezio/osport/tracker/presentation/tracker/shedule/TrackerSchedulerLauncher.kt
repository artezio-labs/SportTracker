package com.artezio.osport.tracker.presentation.tracker.shedule

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.artezio.osport.tracker.util.MINUTE_IN_MILLIS
import com.artezio.osport.tracker.util.SECOND_IN_MILLIS
import com.artezio.osport.tracker.util.WORK_TAG
import java.util.concurrent.TimeUnit

object TrackerSchedulerLauncher {

    fun schedule(
        context: Context,
        eventId: Long,
        startTime: Long,
        duration: Int,
        calibrationTime: Int,
        eventName: String
    ) {
        val dataForStart = Data.Builder()
            .putLong("eventId", eventId)
            .putString("eventName", eventName)
            .putLong("timer_delay", duration * MINUTE_IN_MILLIS + SECOND_IN_MILLIS)
            .build()
        val startPlannedTrainWork =
            OneTimeWorkRequest.Builder(TrackerStartPlanningTrainWorker::class.java)
                .addTag(WORK_TAG)
                .setInputData(dataForStart)
                .setInitialDelay(startTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build()
        WorkManager.getInstance(context)
            .beginWith(startPlannedTrainWork)
            .enqueue()
    }
}