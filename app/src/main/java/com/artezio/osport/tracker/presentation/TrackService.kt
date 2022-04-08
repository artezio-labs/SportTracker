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
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavDeepLinkBuilder
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.prefs.PrefsManager
import com.artezio.osport.tracker.data.trackservice.ServiceLifecycleState
import com.artezio.osport.tracker.data.trackservice.pedometer.StepDetector
import com.artezio.osport.tracker.domain.model.LocationPointData
import com.artezio.osport.tracker.domain.model.PedometerData
import com.artezio.osport.tracker.domain.usecases.InsertLocationDataUseCase
import com.artezio.osport.tracker.domain.usecases.InsertPedometerDataUseCase
import com.artezio.osport.tracker.util.*
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

    private val stepSensor: Sensor by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private var eventId: Long? = null

    private val notificationPendingIntent: PendingIntent by lazy {
        Log.d("event_save", "Event id: $eventId")
        NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.bottom_nav)
            .setDestination(R.id.sessionRecordingFragment)
            .createPendingIntent()
    }

    private var notificationBuilder: NotificationCompat.Builder? = null
    private var sensorEventListener: SensorEventListener? = null


    private var isPaused: Boolean = false

    @Inject
    lateinit var insertLocationDataUseCase: InsertLocationDataUseCase

    @Inject
    lateinit var insertPedometerDataUseCase: InsertPedometerDataUseCase

    @Inject
    lateinit var prefsManager: PrefsManager

    private var timer = Timer()
    private var timeToNotification = 0.0

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
    private val locationRequest: LocationRequest by lazy {
        LocationRequest.create().apply {
            // на адроид 8+, если приложение не в foreground'е, интервал может быть тольше, чем заданное значение
            interval = 1000L
            fastestInterval = 1000L
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private var stepCount = 0

    override fun onCreate() {
        super.onCreate()
        LocalBroadcastManager
            .getInstance(this)
            .registerReceiver(ServiceEchoReceiver(), IntentFilter("ping"))

        val stepsFromPrefs = prefsManager.steps
        Log.d("steps", "Steps from prefs: $stepsFromPrefs")
        stepCount = stepsFromPrefs
    }


    private fun subscribeToLocationUpdates() {
        if (hasLocationAndActivityRecordingPermission(this)) {
            Log.d(STEPS_TAG, "Permissions granted")
            try {
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest, locationCallback, Looper.getMainLooper()
                )
            } catch (ex: SecurityException) {
                Log.e(STEPS_TAG, "Lost location permissions. Couldn't remove updates. $ex")
            }
        }
    }

    private fun runPedometer(id: Long) {
        Log.d(STEPS_TAG, "Step counter doesn't exists, but accelerometer is exists")
        val stepDetector = StepDetector(object : StepDetector.StepListener {
            override fun step(timeNs: Long) {
                if (!isPaused) {
                    stepCount += 1
                }
                stepsLiveData.postValue(stepCount)
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
                receiveSteps(stepCount)
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
        registerListener(sensorManager)
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        Log.d(STEPS_TAG, "onBind: ")
        return localBinder
    }

    override fun onUnbind(intent: Intent?): Boolean = super.onUnbind(intent)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("timer_value", "Service is started")
        when (intent?.action) {
            START_FOREGROUND_SERVICE -> {
                eventId = intent.getLongExtra("eventId", -1L)
                serviceLifecycleState.postValue(ServiceLifecycleState.RUNNING)
                val id = intent.getLongExtra("eventId", -1)
                if (id != -1L) {
                    eventId = id
                } else {
                    Log.d("steps", "Event id not found")
                }
                startForegroundService()
                eventId?.let { if (!isPaused) runPedometer(it) }
                subscribeToLocationUpdates()
                startTimer(0.0, 0)
            }
            STOP_FOREGROUND_SERVICE -> {
                Log.d(STEPS_TAG, "Service stopped!")
                stopForeground(true)
                stopSelf()
                serviceLifecycleState.postValue(ServiceLifecycleState.STOPPED)
            }
            PAUSE_FOREGROUND_SERVICE -> {
                Log.d(STEPS_TAG, "Service paused!")
                sensorManager.unregisterListener(sensorEventListener)
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                serviceLifecycleState.postValue(ServiceLifecycleState.PAUSED)
                isPaused = true
            }
            RESUME_FOREGROUND_SERVICE -> {
                Log.d(STEPS_TAG, "Service resumed!")
                registerListener(sensorManager)
                try {
                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest, locationCallback, Looper.getMainLooper()
                    )
                } catch (ex: SecurityException) {
                    Log.e(STEPS_TAG, "Lost location permissions. Couldn't remove updates. $ex")
                }
                serviceLifecycleState.postValue(ServiceLifecycleState.RESUMED)
                isPaused = false
            }
        }
        return START_NOT_STICKY
    }

    private fun startTimer(time: Double, steps: Int) {
        stepCount = steps
        timer.scheduleAtFixedRate(TimeTask(time), 0, 1000)
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        notificationBuilder = buildNotification(
            this,
            getTimerStringFromDouble(timeToNotification),
        )
        startForeground(FOREGROUND_SERVICE_ID, notificationBuilder?.build())
    }

    private fun buildNotification(
        context: Context,
        time: String,
    ): NotificationCompat.Builder =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_tracker)
            .setContentTitle("Идет запись данных")
            .setContentText(time)
            .setContentIntent(notificationPendingIntent)

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

    private fun registerListener(sensorManager: SensorManager) {
        sensorManager.registerListener(
            sensorEventListener,
            stepSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(STEPS_TAG, "onDestroy: ")
        sensorManager.unregisterListener(sensorEventListener)
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        serviceLifecycleState.postValue(ServiceLifecycleState.STOPPED)
        serviceLifecycleState.postValue(ServiceLifecycleState.NOT_STARTED)
        timer.cancel()
        prefsManager.clearPrefs()
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

    private fun receiveSteps(steps: Int) {
        val intent = Intent(STEPS_UPDATED)
        intent.putExtra(STEPS_EXTRA, steps)
        sendBroadcast(intent)
    }

    private inner class TimeTask(private var time: Double) : TimerTask() {
        override fun run() {
            if (!isPaused) {
                time++
                notificationManager.notify(
                    FOREGROUND_SERVICE_ID,
                    buildNotification(
                        this@TrackService,
                        getTimerStringFromDouble(time)
                    ).build()
                )
                timerValueLiveData.postValue(time)
            }
        }
    }

    companion object {
        private const val CHANNEL_ID = "com.artezio.sporttracker.CHANNEL_ID"
        private const val STEPS = "steps"
        private const val FOREGROUND_SERVICE_ID = 1234
        private const val STEPS_TAG = "STEPS_TAG"
        private const val NOTIFICATION_ID = 19
        private const val NO_SENSOR = "Sorry, sensor doesn't exists on your device"
        const val STEPS_UPDATED = "stepCountUpdated"
        const val STEPS_EXTRA = "stepsExtra"

        val serviceLifecycleState =
            MutableLiveData(ServiceLifecycleState.NOT_STARTED)
        val timerValueLiveData = MutableLiveData(0.0)
        val stepsLiveData = MutableLiveData(0)
    }
}
