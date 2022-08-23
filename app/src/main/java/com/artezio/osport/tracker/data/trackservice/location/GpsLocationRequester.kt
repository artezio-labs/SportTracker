package com.artezio.osport.tracker.data.trackservice.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.artezio.osport.tracker.data.mappers.LocationToLocationPointDataMapper
import com.artezio.osport.tracker.data.preferences.SettingsPreferencesManager
import com.artezio.osport.tracker.util.SECOND_IN_MILLIS
import com.artezio.osport.tracker.util.isLocationPassedFilter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.last
import timber.log.Timber

class GpsLocationRequester(
    context: Context,
    private val prefsManager: SettingsPreferencesManager,
    private val mapper: LocationToLocationPointDataMapper
) : LocationRequester() {

    private val scope = CoroutineScope(Dispatchers.IO) + Job()

    private var locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private lateinit var locationListener: LocationListener

    private var frequency: Long = 0L
    private var distance: Int = 0

    private var lastLocation: Location? = null

    @SuppressLint("MissingPermission")
    override fun subscribeToLocationUpdates(onLocationChanged: (Location) -> Unit) {
        scope.launch {
            frequency = prefsManager.get(true).last() * SECOND_IN_MILLIS
            distance = prefsManager.get(false).last()
        }
        locationListener = LocationListener { location ->
            Timber.d("Location changed: $location")
            if (frequency != 0L || distance != 0) {
                if (lastLocation != null) {
                    if (isLocationPassedFilter(frequency, distance, location, lastLocation!!, mapper)) {
                        onLocationChanged(location)
                    }
                }
            }
            onLocationChanged.invoke(location)
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0F,
            locationListener
        )
    }

    override fun unsubscribeToLocationUpdates() {
        if (::locationListener.isInitialized) {
            locationManager.removeUpdates(locationListener)
        }
    }
}