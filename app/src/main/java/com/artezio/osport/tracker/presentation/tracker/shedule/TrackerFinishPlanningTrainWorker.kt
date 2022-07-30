package com.artezio.osport.tracker.presentation.tracker.shedule

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.artezio.osport.tracker.presentation.TrackService
import com.artezio.osport.tracker.util.STOP_FOREGROUND_SERVICE

class TrackerFinishPlanningTrainWorker(
    private val context: Context,
    private val workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val intent = Intent(context, TrackService::class.java).apply {
            action = STOP_FOREGROUND_SERVICE
        }
        context.stopService(intent)
        return Result.success()
    }
}