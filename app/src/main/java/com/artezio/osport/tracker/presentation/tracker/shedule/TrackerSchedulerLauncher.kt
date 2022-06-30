package com.artezio.osport.tracker.presentation.tracker.shedule

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object TrackerSchedulerLauncher {
    fun launch(
        context: Context,
        startTime: Long,
    ) {
        val data = Data.Builder()
            .putLong("start_time", startTime)
            .build()
        val work = OneTimeWorkRequest.Builder(TrackerWorkerScheduler::class.java)
            .setInputData(data)
            .setInitialDelay(startTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueue(work)
    }
}