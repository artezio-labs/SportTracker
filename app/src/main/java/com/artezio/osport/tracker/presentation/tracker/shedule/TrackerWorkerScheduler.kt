package com.artezio.osport.tracker.presentation.tracker.shedule

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.artezio.osport.tracker.util.MINUTE

class TrackerWorkerScheduler(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun doWork(): Result {
        val startTime = inputData.getLong("start_time", 0L)
        val intentMainNotification = Intent(context, RecordingStartReceiver::class.java).apply {
            putExtra("notification_type", "reminder")
        }

        val pendingIntentFirst = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intentMainNotification,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, startTime, pendingIntentFirst)

        val intentAccuracy = Intent(context, RecordingStartReceiver::class.java).apply {
            putExtra("notification_type", "accuracy")
        }
        val pendingIntentAccuracy = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intentAccuracy,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExact(AlarmManager.RTC, startTime - MINUTE, pendingIntentAccuracy)
        return Result.success()
    }
    companion object {
        private const val REQUEST_CODE = 1092
    }
}