package com.artezio.sporttracker.presentation

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.artezio.sporttracker.R
import com.artezio.sporttracker.data.trackservice.TrackService
import com.artezio.sporttracker.util.START_FOREGROUND_SERVICE

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        showNotification(context, intent)
    }

    private fun showNotification(context: Context, intent: Intent) {
        val startServicePendingIntent = Intent(context, TrackService::class.java).apply {
            putExtra("eventId", intent.getLongExtra(EVENT_ID, -1))
            action = START_FOREGROUND_SERVICE
        }

        val pendingIntent = PendingIntent.getService(context, 0, startServicePendingIntent, 0)

        val notificationId = intent.getIntExtra(NOTIFICATION_ID, -1)

        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL)
            .setSmallIcon(R.drawable.ic_tracker)
            .setContentTitle(intent.getStringExtra(TITLE_NAME))
            .setContentText(intent.getStringExtra(CONTENT_TEXT))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setColor(Color.GREEN)

        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification.build())
    }

    companion object {
        private const val EVENT_ID = "EVENT_ID"
        private const val REMINDER_CHANNEL = "REMINDER_CHANNEL"
        private const val NOTIFICATION_ID = "NOTIFICATION_ID"
        private const val TITLE_NAME = "TITLE_NAME"
        private const val CONTENT_TEXT = "CONTENT_TEXT"
    }
}