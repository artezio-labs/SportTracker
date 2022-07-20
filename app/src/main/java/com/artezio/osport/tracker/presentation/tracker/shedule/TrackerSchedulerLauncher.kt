package com.artezio.osport.tracker.presentation.tracker.shedule

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.artezio.osport.tracker.util.WORK_TAG
import java.util.concurrent.TimeUnit

object TrackerSchedulerLauncher {

    fun schedule(
        context: Context,
        eventId: Long,
        startTime: Long,
        finishTime: Long,
        eventName: String
    ) {
        val dataForStart = Data.Builder()
            .putLong("eventId", eventId)
            .putString("eventName", eventName)
            .build()
        val startPlannedTrainWork =
            OneTimeWorkRequest.Builder(TrackerStartPlanningTrainWorker::class.java)
                .addTag(WORK_TAG)
                .setInputData(dataForStart)
                .setInitialDelay(startTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build()
        val dataForFinish = Data.Builder()
            .putLong("eventId", eventId)
            .build()
        val finishPlannedTrainWork =
            OneTimeWorkRequest.Builder(TrackerFinishPlanningTrainWorker::class.java)
                .addTag(WORK_TAG)
                .setInputData(dataForFinish)
                .setInitialDelay(finishTime - startTime, TimeUnit.MILLISECONDS)
                .build()
        val workRequests = mutableListOf(startPlannedTrainWork, finishPlannedTrainWork)
        WorkManager.getInstance(context)
            .beginWith(startPlannedTrainWork)
            .then(finishPlannedTrainWork)
            .enqueue()
    }
}