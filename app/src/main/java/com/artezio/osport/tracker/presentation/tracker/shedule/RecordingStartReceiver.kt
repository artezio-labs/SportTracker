package com.artezio.osport.tracker.presentation.tracker.shedule

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.presentation.MainActivity
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint

/**
 *  Ресивер для того, чтобы запланировать нотификацию
 */
@AndroidEntryPoint
class RecordingStartReceiver : BroadcastReceiver() {

    private val locationRequest = LocationRequest.create().apply {
        interval = 1000L
        fastestInterval = 1000L
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            val currentLocation = result.lastLocation
            Log.d("broadcast_location", "Accuracy: ${currentLocation.accuracy}")
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.getStringExtra("notification_type")
        if (action == "reminder") {

            showNotification(context, intent)
        } else {
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            requestLocationUpdates(fusedLocationProviderClient)
        }
    }

    private fun showNotification(context: Context, intent: Intent) {

        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val pendingIntent = NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.bottom_nav)
            .setDestination(R.id.sessionRecordingFragment)
            .createPendingIntent()

        val notificationId = intent.getIntExtra(NOTIFICATION_ID, -1)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(manager, CHANNEL_ID)
        }

        val notification: Notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_event_alarm)
            .setContentTitle(context.getString(R.string.warning_text))
            .setContentText(context.getString(R.string.its_time_to_start_recording))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setAutoCancel(true)
            .build()
        manager.notify(notificationId, notification)

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

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates(fusedLocationProvider: FusedLocationProviderClient) {
        fusedLocationProvider.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    companion object {
        private const val EVENT_ID = "EVENT_ID"
        private const val REMINDER_CHANNEL = "REMINDER_CHANNEL"
        private const val NOTIFICATION_ID = "NOTIFICATION_FIRST_ID"
        private const val NOTIFICATION_MAIN_ID = "NOTIFICATION_MAIN_ID"
        private const val TITLE_NAME = "TITLE_NAME"
        private const val CONTENT_TEXT = "CONTENT_TEXT"
        private const val NOTIFICATION_CHANNEL_NAME = "EVENT_REMINDER"
        private const val NOTIFICATION_CHANNEL_DESCRIPTION = "reminder"
        private const val CHANNEL_ID = "com.artezio.sporttracker.REMINDER_CHANNEL_ID"
    }
}