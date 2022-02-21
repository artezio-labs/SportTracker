package com.artezio.osport.tracker.presentation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.util.START_FOREGROUND_SERVICE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        showNotification(context, intent)
        Log.d("steps", "onReceive: ")
    }

    private fun showNotification(context: Context, intent: Intent) {

        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val startServicePendingIntent = Intent(context, TrackService::class.java).apply {
            putExtra("eventId", intent.getLongExtra(EVENT_ID, -1))
            action = START_FOREGROUND_SERVICE
        }

        val pendingIntent = PendingIntent.getService(context, 84573, startServicePendingIntent, 0)
        val notificationId = intent.getIntExtra(NOTIFICATION_ID, -1)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(manager, CHANNEL_ID)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_event_alarm)
            .setContentTitle(intent.getStringExtra(TITLE_NAME))
            .setContentText(intent.getStringExtra(CONTENT_TEXT))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setAutoCancel(true)

        manager.notify(notificationId, notification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        notificationManager: NotificationManager,
        notificationChannelId: String
    ) {
        if (notificationManager.getNotificationChannel(notificationChannelId) == null) {
            val notificationChannel = NotificationChannel(
                notificationChannelId,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = NOTIFICATION_CHANNEL_DESCRIPTION
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val EVENT_ID = "EVENT_ID"
        private const val REMINDER_CHANNEL = "REMINDER_CHANNEL"
        private const val NOTIFICATION_ID = "NOTIFICATION_ID"
        private const val TITLE_NAME = "TITLE_NAME"
        private const val CONTENT_TEXT = "CONTENT_TEXT"
        private const val NOTIFICATION_CHANNEL_NAME = "EVENT_REMINDER"
        private const val NOTIFICATION_CHANNEL_DESCRIPTION = "reminder"
        private const val CHANNEL_ID = "com.artezio.sporttracker.REMINDER_CHANNEL_ID"
    }
}