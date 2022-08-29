package com.artezio.osport.tracker.data.trackservice

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.presentation.MainActivity
import com.artezio.osport.tracker.presentation.TrackService
import com.artezio.osport.tracker.util.*
import timber.log.Timber

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
        paused: Boolean,
        notificationPendingIntent: PendingIntent,
    ): NotificationCompat.Builder {
        val title: String
        val action = if (paused) {
            val intent = Intent(context, TrackService::class.java).apply {
                action = RESUME_FOREGROUND_SERVICE
            }
            val pendingIntent = PendingIntent.getService(
                context,
                PENDING_INTENT_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            title = context.getString(R.string.notification_paused)
            NotificationCompat.Action(R.drawable.ic_resume, RESUME_RECORDING, pendingIntent)
        } else {
            val intent = Intent(context, TrackService::class.java).apply {
                action = PAUSE_FOREGROUND_SERVICE
            }
            val pendingIntent = PendingIntent.getService(
                context,
                PENDING_INTENT_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            title = context.getString(R.string.notification_resumed)
            NotificationCompat.Action(
                R.drawable.ic_baseline_pause_24,
                PAUSE_RECORDING,
                pendingIntent
            )
        }
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_tracker)
            .setContentTitle(title)
            .setContentText("$time - $distance")
            .addAction(action)
            .setAutoCancel(true)
            .setContentIntent(notificationPendingIntent)
    }

    fun buildNotification(time: Double, distance: Double, paused: Boolean): Notification {
        Timber.d("Notification was built")
        return buildNotificationView(
            getTimerStringFromDouble(time),
            distanceToString(distance),
            paused,
            notificationPendingIntent,
        ).build()
    }

    fun notify(time: Double, distance: Double, paused: Boolean = false) {
        Timber.d("Recording notification updated")
        notificationManager.notify(
            FOREGROUND_SERVICE_ID,
            buildNotification(
                time,
                distance,
                paused
            )
        )
    }

    fun buildGpsCalibrationNotification(time: Long): Notification {
        Timber.d("Gps calibration notification was built")
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_calibration)
            .setContentTitle("Калибровка GPS датчика")
            .setContentText("До конца калибровки осталось: ${formatTimeToNotification(time)}")
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(false)
            .build()
    }


    fun notify(time: Long) {
        Timber.d("Notify time: $time")
        notificationManager.notify(
            FOREGROUND_SERVICE_ID,
            buildGpsCalibrationNotification(
                time
            )
        )
    }

    companion object {
        private const val CHANNEL_ID = "com.artezio.sporttracker.CHANNEL_ID"
        private const val PLANNED_CHANNEL_ID = "com.artezio.sporttracker.PLANNED_CHANNEL_ID"
        private const val FOREGROUND_SERVICE_ID = 1234
        private const val PENDING_INTENT_REQUEST_CODE = 123456
        private const val RESUME_RECORDING = "Возобновить запись"
        private const val PAUSE_RECORDING = "Приостановить запись"
        private const val CALIBRATION = "Приостановить запись"
    }
}