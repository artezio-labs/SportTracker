package com.artezio.osport.tracker.data.trackservice

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.presentation.MainActivity
import com.artezio.osport.tracker.presentation.TrackService
import com.artezio.osport.tracker.util.PAUSE_FOREGROUND_SERVICE
import com.artezio.osport.tracker.util.RESUME_FOREGROUND_SERVICE
import com.artezio.osport.tracker.util.distanceToString
import com.artezio.osport.tracker.util.getTimerStringFromDouble

class ServiceNotificationBuilder(
    private val context: Context
) {

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val notificationPendingIntent: PendingIntent =
        NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.bottom_nav)
            .setDestination(R.id.sessionRecordingFragment)
            .createPendingIntent()

    private fun buildNotificationView(
        time: String,
        distance: String,
        notificationPendingIntent: PendingIntent,
    ): NotificationCompat.Builder {
        val action = if (TrackService.serviceLifecycleState.value == ServiceLifecycleState.PAUSED) {
            val intent = Intent(context, TrackService::class.java).apply {
                action = RESUME_FOREGROUND_SERVICE
            }
            val pendingIntent = PendingIntent.getService(
                context,
                PENDING_INTENT_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            NotificationCompat.Action.Builder(R.drawable.ic_resume, RESUME_RECORDING, pendingIntent).build()
        } else {
            val intent = Intent(context, TrackService::class.java).apply {
                action = PAUSE_FOREGROUND_SERVICE
            }
            val pendingIntent = PendingIntent.getService(
                context,
                PENDING_INTENT_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            NotificationCompat.Action.Builder(R.drawable.ic_baseline_pause_24, PAUSE_RECORDING, pendingIntent).build()
        }
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_tracker)
            .setContentTitle("Идет запись данных")
            .setContentText("$time - $distance")
            .addAction(action)
            .setContentIntent(notificationPendingIntent)
    }

    fun buildNotification(time: Double, distance: Double): Notification {
        Log.d("notification_builder", "Time: $time, distance: $distance")
        return buildNotificationView(
            getTimerStringFromDouble(time),
            distanceToString(distance),
            notificationPendingIntent,
        ).build()
    }

    fun notify(time: Double, distance: Double) {
        notificationManager.cancel(FOREGROUND_SERVICE_ID)
        notificationManager.notify(
            FOREGROUND_SERVICE_ID,
            buildNotification(
                time,
                distance
            )
        )
    }

    companion object {
        private const val CHANNEL_ID = "com.artezio.sporttracker.CHANNEL_ID"
        private const val FOREGROUND_SERVICE_ID = 1234
        private const val PENDING_INTENT_REQUEST_CODE = 123456
        private const val RESUME_RECORDING = "Возобновить запись"
        private const val PAUSE_RECORDING = "Приостановить запись"
    }
}