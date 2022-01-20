package com.artezio.sporttracker.data.trackservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.artezio.sporttracker.R
import com.artezio.sporttracker.presentation.MainActivity

// сервис для шагомера
// но возможно здесь же буду делать всё остальное
// надо над этим подумать
class TrackService : Service() {

    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private var notificationBuilder: NotificationCompat.Builder? = null
    private var sensorEventListener: SensorEventListener? = null

    private var stepCount: Int = 0

    override fun onCreate() {
        super.onCreate()

        startForegroundService()

        var stepSensor: Sensor? = null
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)) {
            Log.d(STEPS_TAG, "Step counter sensor is exists")
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            sensorEventListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    event?.let { e ->
                        stepCount = e.values[0].toInt()
                        Log.d(STEPS_TAG, "Steps: $stepCount")
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    // это нам не нужно
                }

            }
        } else if (packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)) {
            Log.d(STEPS_TAG, "Step counter doesn't exists, but accelerometer is exists")
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val stepDetector = StepDetector(object : StepDetector.StepListener {
                override fun step(timeNs: Long) {
                    stepCount += 1
                    Log.d(STEPS_TAG, "Steps: $stepCount")
                }
            })
            sensorEventListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                        stepDetector.updateAccel(
                            event.timestamp, event.values[0], event.values[1], event.values[2]
                        )
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
        }
        if (stepSensor != null) {
            sensorManager.registerListener(
                sensorEventListener,
                stepSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        } else {
            Toast.makeText(this, NO_SENSOR, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBind(intent: Intent): IBinder? = null
    override fun onUnbind(intent: Intent?): Boolean = super.onUnbind(intent)


    private fun startForegroundService() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            0
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // todo поменять вид нотификации
            .setContentTitle(getString(R.string.app_name))
            .setContentIntent(pendingIntent)
        startForeground(FOREGROUND_SERVICE_ID, notificationBuilder?.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_NONE
            )
            notificationChannel.description = STEPS
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "com.artezio.sporttracker.CHANNEL_ID"
        private const val STEPS = "steps"
        private const val FOREGROUND_SERVICE_ID = 1234
        private const val STEPS_TAG = "STEPS_TAG"
        private const val NO_SENSOR = "Sorry, sensor doesn't exists on your device"
    }
}