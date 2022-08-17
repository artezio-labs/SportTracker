package com.artezio.osport.tracker.presentation.tracker.shedule

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.artezio.osport.tracker.util.MINUTE_IN_MILLIS
import com.artezio.osport.tracker.util.SECOND_IN_MILLIS
import com.artezio.osport.tracker.util.WORK_TAG
import com.artezio.osport.tracker.util.formatEventName
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
        val startTimeWithCalibrationTime = startTime - (System.currentTimeMillis() + (calibrationTime * SECOND_IN_MILLIS))
        Log.d("gps_calibration", "Planned calibration date: ${formatEventName(startTimeWithCalibrationTime)}\n calibration time: $calibrationTime")
        val dataForStart = Data.Builder()
            .putLong("eventId", eventId)
            .putString("eventName", eventName)
            .putLong("timer_delay", duration * MINUTE_IN_MILLIS + MINUTE_IN_MILLIS)
            .putLong("calibration_time", calibrationTime * SECOND_IN_MILLIS)
            .build()
        val startPlannedTrainWork =
            OneTimeWorkRequest.Builder(TrackerStartPlanningTrainWorker::class.java)
                .addTag(WORK_TAG)
                .setInputData(dataForStart)
                .setInitialDelay(startTimeWithCalibrationTime, TimeUnit.MILLISECONDS)
                .build()
        WorkManager.getInstance(context)
            .enqueue(startPlannedTrainWork)
    }
}