package com.artezio.osport.tracker.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
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
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.trackservice.ServiceLifecycleState
import com.artezio.osport.tracker.data.trackservice.ServiceNotificationBuilder
import com.artezio.osport.tracker.data.trackservice.TrackServiceDataManager
import com.artezio.osport.tracker.data.trackservice.location.GpsLocationRequester
import com.artezio.osport.tracker.data.trackservice.location.LocationRequester
import com.artezio.osport.tracker.data.trackservice.pedometer.StepDetector
import com.artezio.osport.tracker.domain.model.LocationPointData
import com.artezio.osport.tracker.domain.model.PedometerData
import com.artezio.osport.tracker.domain.usecases.ObserveDistanceUseCase
import com.artezio.osport.tracker.domain.usecases.UpdateEventUseCase
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

    private val notificationBuilder: ServiceNotificationBuilder by lazy {
        ServiceNotificationBuilder(this)
    }
    private var sensorEventListener: SensorEventListener? = null

    private var stepDetector: StepDetector? = null


    private var isPaused: Boolean = false

    private var planned = false

    @Inject
    lateinit var updateEventUseCase: UpdateEventUseCase

    @Inject
    lateinit var observeDistanceUseCase: ObserveDistanceUseCase

    @Inject
    lateinit var trackServiceDataManager: TrackServiceDataManager

    @Inject
    lateinit var locationRequester: GpsLocationRequester

    private var timer = Timer()
    private var timeToNotification = 0.0
    private var distanceToNotification = 0.0

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
            trackServiceDataManager.insertLocationData(locationPoint)
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

    }

    private fun subscribeToLocationUpdates() {
        if (hasLocationAndActivityRecordingPermission(this)) {
            Log.d(STEPS_TAG, "Permissions granted")
            try {
                if (LocationRequester.checkIsGmsAvailable(this)) {
                    Log.d(
                        STEPS_TAG,
                        "GMS is available: ${LocationRequester.checkIsGmsAvailable(this)}"
                    )
                    serviceIoScope.launch {
                        fusedLocationProviderClient.requestLocationUpdates(
                            locationRequest, locationCallback, Looper.getMainLooper()
                        )
                    }
                } else {
                    subscribeToGpsLocationUpdates()
                }
            } catch (ex: SecurityException) {
                Log.e(STEPS_TAG, "Lost location permissions. Couldn't remove updates. $ex")
            }
        }
    }

    private fun runPedometer(id: Long) {
        Log.d(STEPS_TAG, "Step counter doesn't exists, but accelerometer is exists")
        stepDetector = StepDetector(object : StepDetector.StepListener {
            override fun step(timeNs: Long) {
                if (!isPaused) {
                    stepCount += 1

                }
                stepsLiveData.postValue(stepCount)
                Log.d(STEPS_TAG, "Steps: $stepCount time: $timeNs ns")
                Log.d(STEPS_TAG, "Steps from livedata: ${stepsLiveData.value}")
                val data = PedometerData(
                    stepsLiveData.value!!,
                    System.currentTimeMillis(),
                    id
                )
                trackServiceDataManager.insertPedometerData(data)
            }
        })
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    stepDetector?.updateAccel(
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
                onStartService(intent)
            }
            START_PLANNED_SERVICE -> {
                onStartService(intent)
                val delay = intent.getLongExtra("timer_delay", 0L)
                if (delay != 0L) {
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            sensorManager.unregisterListener(sensorEventListener)
                            removeLocationUpdates()
                            stopSelf()
                            stopForeground(true)
                            serviceLifecycleState.postValue(ServiceLifecycleState.STOPPED)
                        }
                    }, delay)
                } else {
                    Log.d("timer_delay", "Wrong delay: $delay")
                }
            }
            STOP_FOREGROUND_SERVICE -> {
                Log.d(STEPS_TAG, "Service stopped!")
                sensorManager.unregisterListener(sensorEventListener)
                removeLocationUpdates()
                stopSelf()
                stopForeground(true)
                serviceLifecycleState.postValue(ServiceLifecycleState.STOPPED)
            }
            PAUSE_FOREGROUND_SERVICE -> {
                Log.d(STEPS_TAG, "Service paused!")
                sensorManager.unregisterListener(sensorEventListener)
                removeLocationUpdates()
                serviceLifecycleState.postValue(ServiceLifecycleState.PAUSED)
                isPaused = true
                timerValueLiveData.value?.let {
                    notificationBuilder.notify(
                        it,
                        distanceToNotification
                    )
                }
            }
            RESUME_FOREGROUND_SERVICE -> {
                Log.d(STEPS_TAG, "Service resumed!")
                registerListener(sensorManager)
                try {
                    if (LocationRequester.checkIsGmsAvailable(this)) {
                        fusedLocationProviderClient.requestLocationUpdates(
                            locationRequest, locationCallback, Looper.getMainLooper()
                        )
                    } else {
                        subscribeToGpsLocationUpdates()
                    }

                } catch (ex: SecurityException) {
                    Log.e(STEPS_TAG, "Lost location permissions. Couldn't remove updates. $ex")
                }
                serviceLifecycleState.postValue(ServiceLifecycleState.RESUMED)
                isPaused = false
                timerValueLiveData.value?.let {
                    notificationBuilder.notify(
                        it,
                        distanceToNotification
                    )
                }
            }
        }
        return START_STICKY
    }

    private fun onStartService(intent: Intent) {
        eventId = intent.getLongExtra("eventId", -1L)
        serviceLifecycleState.postValue(ServiceLifecycleState.RUNNING)
        val id = intent.getLongExtra("eventId", -1)
        if (id != -1L) {
            eventId = id
        } else {
            Log.d("steps", "Event id not found")
        }
        startForegroundService()

        eventId?.let {
            if (!isPaused) runPedometer(it)
        }
        subscribeToLocationUpdates()
        startTimer(0.0, 0)
        eventId?.let {
            Log.d("observe_distance", "Event: $it")
            lifecycleScope.launch {
                observeDistanceUseCase.execute(it).collect { distance ->
                    distanceToNotification = distance
                    Log.d(
                        "observe_distance",
                        "Distance flow: $distance\n from notification: $distanceToNotification"
                    )
                }
            }
        }
    }

    private fun subscribeToGpsLocationUpdates() {
        locationRequester.subscribeToLocationUpdates { location ->
            val locationPoint = LocationPointData(
                location.latitude,
                location.longitude,
                location.altitude,
                location.accuracy,
                location.speed,
                System.currentTimeMillis(),
                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY),
                eventId ?: -1L
            )
            Log.d(STEPS_TAG, "onLocationResult: $locationPoint")
            trackServiceDataManager.insertLocationData(locationPoint)
        }
    }

    private fun startTimer(time: Double, steps: Int) {
        stepCount = steps
        lifecycleScope.launch(Dispatchers.IO) { timer.scheduleAtFixedRate(TimeTask(time), 0, 1000) }
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        val notification =
            notificationBuilder.buildNotification(timeToNotification, distanceToNotification)
        startForeground(FOREGROUND_SERVICE_ID, notification)
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

    private fun registerListener(sensorManager: SensorManager) {
        sensorManager.registerListener(
            sensorEventListener,
            stepSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    private fun removeLocationUpdates() {
        if (LocationRequester.checkIsGmsAvailable(this)) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        } else {
            locationRequester.unsubscribeToLocationUpdates()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(STEPS_TAG, "onDestroy: ")
        eventId?.let { trackServiceDataManager.updateEvent(it) }
        sensorManager.unregisterListener(sensorEventListener)
        serviceLifecycleState.postValue(ServiceLifecycleState.STOPPED)
        serviceLifecycleState.postValue(ServiceLifecycleState.NOT_STARTED)
        stepsLiveData.postValue(0)
        stepDetector = null
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
            if (!isPaused) {
                time++
                timerValueLiveData.postValue(time)
                timerValueLiveData.value?.let {
                    notificationBuilder.notify(
                        it,
                        distanceToNotification
                    )
                }
            }
        }
    }

    companion object {
        private const val CHANNEL_ID = "com.artezio.sporttracker.CHANNEL_ID"
        private const val STEPS = "steps"
        private const val FOREGROUND_SERVICE_ID = 1234
        private const val STEPS_TAG = "STEPS_TAG"

        val serviceLifecycleState =
            MutableLiveData(ServiceLifecycleState.NOT_STARTED)
        val timerValueLiveData = MutableLiveData(0.0)
        val stepsLiveData = MutableLiveData(0)
    }
}