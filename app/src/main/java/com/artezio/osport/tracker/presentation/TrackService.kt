package com.artezio.osport.tracker.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.trackservice.ServiceLifecycleState
import com.artezio.osport.tracker.data.trackservice.pedometer.StepDetector
import com.artezio.osport.tracker.domain.model.LocationPointData
import com.artezio.osport.tracker.domain.model.PedometerData
import com.artezio.osport.tracker.domain.usecases.InsertLocationDataUseCase
import com.artezio.osport.tracker.domain.usecases.InsertPedometerDataUseCase
import com.artezio.osport.tracker.util.START_FOREGROUND_SERVICE
import com.artezio.osport.tracker.util.STOP_FOREGROUND_SERVICE
import com.artezio.osport.tracker.util.hasLocationAndActivityRecordingPermission
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TrackService : LifecycleService() {

    private val serviceJob = Job()
    private val serviceIoScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private val batteryManager: BatteryManager by lazy {
        getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    }

    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private var notificationBuilder: NotificationCompat.Builder? = null
    private var sensorEventListener: SensorEventListener? = null


    private var eventId: Long? = null

    @Inject
    lateinit var insertLocationDataUseCase: InsertLocationDataUseCase

    @Inject
    lateinit var insertPedometerDataUseCase: InsertPedometerDataUseCase

    private val timer = Timer()

    private val localBinder = LocalBinder()

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            val lastLocation = result.lastLocation
            val locationPoint = LocationPointData(
                lastLocation.latitude,
                lastLocation.longitude,
                lastLocation.altitude,
                lastLocation.accuracy,
                lastLocation.speed,
                System.currentTimeMillis(),
                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY),
                eventId ?: -1L
            )
            Log.d(STEPS_TAG, "onLocationResult: $locationPoint")
            serviceIoScope.launch {
                insertLocationDataUseCase.execute(locationPoint)
            }
        }
    }
    private var locationRequest: LocationRequest? = null

    private var stepCount = 0

    override fun onCreate() {
        super.onCreate()
        LocalBroadcastManager
            .getInstance(this)
            .registerReceiver(ServiceEchoReceiver(), IntentFilter("ping"))
    }


    private fun subscribeToLocationUpdates() {
        if (hasLocationAndActivityRecordingPermission(this)) {
            Log.d(STEPS_TAG, "Permissions granted")
            locationRequest = LocationRequest.create().apply {
                // на адроид 8+, если приложение не в foreground'е, интервал может быть тольше, чем заданное значение
                interval = 1000L
                fastestInterval = 1000L
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            try {
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest!!, locationCallback, Looper.getMainLooper()
                )
            } catch (ex: SecurityException) {
                Log.e(STEPS_TAG, "Lost location permissions. Couldn't remove updates. $ex")
            }
        }
    }

    private fun runPedometer(id: Long) {
        Log.d(STEPS_TAG, "Step counter doesn't exists, but accelerometer is exists")
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val stepDetector = StepDetector(object : StepDetector.StepListener {
            override fun step(timeNs: Long) {
                stepCount += 1
                Log.d(STEPS_TAG, "Steps: $stepCount")
                serviceIoScope.launch {
                    insertPedometerDataUseCase.execute(
                        PedometerData(
                            stepCount,
                            System.currentTimeMillis(),
                            id
                        )
                    )
                }
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
        if (stepSensor != null) {
            val register = sensorManager.registerListener(
                sensorEventListener,
                stepSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            Log.d(STEPS_TAG, "Is listener registered: $register")
        } else {
            Toast.makeText(this, NO_SENSOR, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        Log.d(STEPS_TAG, "onBind: ")

        return localBinder
    }

    override fun onUnbind(intent: Intent?): Boolean = super.onUnbind(intent)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            START_FOREGROUND_SERVICE -> {
                startForegroundService()
                eventId = intent.getLongExtra("eventId", -1L)
                serviceLifecycleState.postValue(ServiceLifecycleState.Running)
            }
            STOP_FOREGROUND_SERVICE -> {
                Log.d(STEPS_TAG, "Service stopped!")
                stopForeground(true)
                stopSelf()
                serviceLifecycleState.postValue(ServiceLifecycleState.Stopped)
            }
        }

        val id = intent?.getLongExtra("eventId", -1)
        if (id != -1L) {
            eventId = id
        } else {
            Log.d("steps", "Event id not found")
        }

        eventId?.let { runPedometer(it) }
        subscribeToLocationUpdates()
        startTimer(intent)
        return START_NOT_STICKY
    }

    private fun startTimer(intent: Intent?) {
        val time = intent?.getDoubleExtra(TIME_EXTRA, 0.0) ?: 0.0
        timer.scheduleAtFixedRate(TimeTask(time), 0, 1000)
    }

    private fun startForegroundService() {
        val notificationIntent = Intent(this, TrackService::class.java).apply {
            action = STOP_FOREGROUND_SERVICE
        }
        val pendingIntent = PendingIntent.getService(
            this,
            0,
            notificationIntent,
            0
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_tracker)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Идет запись данных")
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

    override fun onDestroy() {
        super.onDestroy()
        Log.d(STEPS_TAG, "onDestroy: ")
        sensorManager.unregisterListener(sensorEventListener)
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        serviceLifecycleState.postValue(ServiceLifecycleState.Stopped)
        timer.cancel()
    }

    inner class LocalBinder : Binder() {
        internal val service: TrackService
            get() = this@TrackService
    }

    inner class ServiceEchoReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            LocalBroadcastManager
                .getInstance(this@TrackService)
                .sendBroadcastSync(Intent("pong"))
        }
    }

    private inner class TimeTask(private var time: Double) : TimerTask() {
        override fun run() {
            val intent = Intent(TIMER_UPDATED)
            time++
            intent.putExtra(TIME_EXTRA, time)
            sendBroadcast(intent)
        }
    }

    companion object {
        private const val CHANNEL_ID = "com.artezio.sporttracker.CHANNEL_ID"
        private const val STEPS = "steps"
        private const val FOREGROUND_SERVICE_ID = 1234
        private const val STEPS_TAG = "STEPS_TAG"
        private const val NO_SENSOR = "Sorry, sensor doesn't exists on your device"
        const val TIMER_UPDATED = "timerUpdated"
        const val TIME_EXTRA = "timeExtra"

        val serviceLifecycleState = MutableLiveData<ServiceLifecycleState>(ServiceLifecycleState.Stopped)

    }
}