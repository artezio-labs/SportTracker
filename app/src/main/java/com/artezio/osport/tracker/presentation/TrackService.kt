package com.artezio.osport.tracker.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.*
import android.speech.tts.SynthesisCallback
import android.speech.tts.SynthesisRequest
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeechService
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.preferences.SettingsPreferencesManager
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
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TrackService : TextToSpeechService() {

    private val serviceJob = Job()
    private val serviceIoScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private val lifecycleScope =
        CoroutineScope(Dispatchers.Main + SupervisorJob() + CoroutineExceptionHandler { context, throwable ->
            Log.e("service_lifecycle_scope", "Exception $throwable in context: $context")
        })

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

    private val tts: TextToSpeech by lazy {
        TextToSpeech(this@TrackService) { status ->
            if (status != TextToSpeech.ERROR) {
                tts.language = Locale.getDefault()
                Log.d("text_to_speech", "TTS SUCCESS")
            } else {
                Log.d("text_to_speech", "TTS ERROR")
            }
        }
    }

    private var eventId: Long? = null

    private val notificationBuilder: ServiceNotificationBuilder by lazy {
        ServiceNotificationBuilder(this)
    }
    private var sensorEventListener: SensorEventListener? = null

    private var stepDetector: StepDetector? = null


    private var isPaused: Boolean = false

    private var isCalibrating: Boolean = false

    private var isPlanned = false

    private var calibrationTimeToNotification: Long = 0

    @Inject
    lateinit var updateEventUseCase: UpdateEventUseCase

    @Inject
    lateinit var observeDistanceUseCase: ObserveDistanceUseCase

    @Inject
    lateinit var trackServiceDataManager: TrackServiceDataManager

    @Inject
    lateinit var locationRequester: GpsLocationRequester

    @Inject
    lateinit var settingsPreferencesManager: SettingsPreferencesManager

    private var timer = Timer()
    private var timeToNotification = 0.0
    private var distanceToNotification = 0.0
    private var timerIsFinished = false
    private var continuation = 0.0

    private var distanceFilter = 0
    private var previousLocation: Location? = null

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
            if (isCalibrating) {
                Log.d(STEPS_TAG, "onLocationResultAccuracy: ${locationPoint.accuracy}")
                calibrationAccuracyState.postValue(lastLocation.accuracy)
            } else {
                Log.d(STEPS_TAG, "onLocationResultAccuracy: ${locationPoint.accuracy}")
                previousLocation?.let {
                    val distance = it.distanceTo(lastLocation)
                    if (distance < distanceFilter) {
                        trackServiceDataManager.insertLocationData(locationPoint)
                    }
                }
                previousLocation = lastLocation
            }
        }
    }
    private val locationRequest: LocationRequest by lazy {
        var initialInterval = 1000L
        if (this::settingsPreferencesManager.isInitialized) {
            lifecycleScope.launch {
                settingsPreferencesManager.get(true).collect {
                    initialInterval = it.toLong() * SECOND_IN_MILLIS
                }
            }
        }
        Log.d("track_settings", "initialInterval: $initialInterval")
        LocationRequest.create().apply {
            // на адроид 8+, если приложение не в foreground'е, интервал может быть тольше, чем заданное значение
            interval = initialInterval
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private var stepCount = 0

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

    override fun onIsLanguageAvailable(lang: String?, country: String?, variant: String?): Int {
        return tts.isLanguageAvailable(Locale.getDefault())
    }

    override fun onGetLanguage(): Array<String> {
        return tts.availableLanguages.map { it.language }.toTypedArray()
    }

    override fun onLoadLanguage(lang: String?, country: String?, variant: String?): Int {
        return tts.setLanguage(Locale.getDefault())
    }

    override fun onStop() {
        if (tts != null) {
            tts.stop()
            tts.shutdown()
        }
    }

    override fun onSynthesizeText(request: SynthesisRequest?, callback: SynthesisCallback?) {
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("timer_value", "Service is started")
        Timber.d("Service started")
        when (intent?.action) {
            START_FOREGROUND_SERVICE -> {
                Timber.d("Service recording started")
                Timber.d("Service is foreground: ${isForeground()}")
                onStartService(intent)
            }
            START_PLANNED_SERVICE -> {
                isPlanned = true
                Log.d("service_timers", "planned train started")
                serviceLifecycleState.postValue(ServiceLifecycleState.CALIBRATING)
                isCalibrating = true
                val calibrationTime = intent.getLongExtra("calibration_time", 60 * SECOND_IN_MILLIS)
                val delay = intent.getLongExtra("timer_delay", 0L)
                continuation = delay.toDouble()
                Log.d("gps_calibration", "Delay: $delay")
                if (delay != 0L) {
                    startForegroundService()
                    Timber.d("Service is foreground: ${isForeground()}")
                    Log.d("service_type", "is foreground: ${isForeground()}")
                    subscribeToLocationUpdates()
//                    speaker.speak(Phrase("calibration_start", "Time to start: ${calibrationTime / SECOND_IN_MILLIS}"))
                    Handler(Looper.getMainLooper()).postDelayed({
                        tts.speak(
                            "До начала забега ${calibrationTime / SECOND_IN_MILLIS} секунд",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                    }, 1000)
                    object : CountDownTimer(calibrationTime, SECOND_IN_MILLIS) {
                        override fun onTick(p0: Long) {
                            Log.d("service_timers", "calibration timer starts")
                            calibrationTimeToNotification = p0 / SECOND_IN_MILLIS
                            calibrationTimeState.postValue(p0 / SECOND_IN_MILLIS)
                            Log.d(
                                "gps_calibration",
                                "calibrationTimeToNotification: $calibrationTimeToNotification \n onTick: $p0"
                            )
                            notificationBuilder.notify(calibrationTimeToNotification)
                        }

                        override fun onFinish() {
                            Timber.d("Count down timer onFinish() was called")
                            timerIsFinished = true
                            runRecordingAfterCalibration(intent)
                            tts.speak(
                                "Начало забега",
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                null
                            )
                        }
                    }.start()
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            tts.speak(
                                "Завершение забега",
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                null
                            )
                            Timber.d("Service stopped")
                            sensorManager.unregisterListener(sensorEventListener)
                            removeLocationUpdates()
                            stopSelf()
                            stopForeground(true)
                            serviceLifecycleState.postValue(ServiceLifecycleState.STOPPED)
                        }
                    }, delay)
                } else {
                    Timber.d("Tracker delay: $delay")
                }
            }
            STOP_FOREGROUND_SERVICE -> {
                Timber.d("Service has been stopped")
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
                        distanceToNotification,
                        true
                    )
                }
            }
            RESUME_FOREGROUND_SERVICE -> {
                Timber.d("Service has been resumed")
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

    private fun runRecordingAfterCalibration(intent: Intent) {
        if (serviceLifecycleState.value != ServiceLifecycleState.RUNNING) {
            isCalibrating = false
            onStartService(intent, true)
            serviceLifecycleState.postValue(ServiceLifecycleState.RUNNING)
        }
    }

    private fun onStartService(intent: Intent, isAlreadyForegroundStarted: Boolean = false) {
        Timber.d("onStartService() was called, recording started")
        eventId = intent.getLongExtra("eventId", -1L)
        eventId?.let { currentEventIdLiveData.postValue(it) }
        serviceLifecycleState.postValue(ServiceLifecycleState.RUNNING)
        lifecycleScope.launch {
            settingsPreferencesManager.get(false).collect {
                distanceFilter = it
            }
        }
        val id = intent.getLongExtra("eventId", -1)
        if (id != -1L) {
            eventId = id
        } else {
            Log.d("steps", "Event id not found")
        }
        if (!isAlreadyForegroundStarted) {
            startForegroundService()
            Log.d("service_type", "is foreground: ${isForeground()}")
            subscribeToLocationUpdates()
        }

        eventId?.let {
            if (!isPaused) runPedometer(it)
        }

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
        Timber.d("Subscribed to location updates")
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
        Timber.d("Recording timer started")
        stepCount = steps
        lifecycleScope.launch(Dispatchers.IO) { timer.scheduleAtFixedRate(TimeTask(time), 0, 1000) }
    }

    private fun startForegroundService() {
        Timber.d("startForegroundService() was called, foreground service started!")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        val notification = if (isCalibrating) {
            notificationBuilder.buildGpsCalibrationNotification(calibrationTimeToNotification)
        } else {
            notificationBuilder.buildNotification(timeToNotification, distanceToNotification, false)
        }
        startForeground(FOREGROUND_SERVICE_ID, notification)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.description = STEPS
            notificationManager.createNotificationChannel(notificationChannel)
            Timber.d("Notification channel was created")
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
        Timber.d("Service onDestroy()")
        eventId?.let { trackServiceDataManager.updateEvent(it) }
        sensorManager.unregisterListener(sensorEventListener)
        serviceLifecycleState.postValue(ServiceLifecycleState.STOPPED)
        serviceLifecycleState.postValue(ServiceLifecycleState.NOT_STARTED)
        stepsLiveData.postValue(0)
        stepDetector = null
        timer.cancel()
        currentEventIdLiveData.postValue(-1L)
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
                Timber.d("Timer recording onTick: $time")
                timerValueLiveData.postValue(time)
                timerValueLiveData.value?.let {
                    notificationBuilder.notify(
                        it,
                        distanceToNotification
                    )
                }
                if (isPlanned && continuation != 0.0 && ((continuation * 60) / time) == 2.0) {
                    tts.speak(
                        "Середина забега",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
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
        val currentEventIdLiveData =
            MutableLiveData(-1L)
        val timerValueLiveData = MutableLiveData(0.0)
        val stepsLiveData = MutableLiveData(0)
        val calibrationTimeState = MutableLiveData(0L)
        val calibrationAccuracyState = MutableLiveData(0F)
    }
}