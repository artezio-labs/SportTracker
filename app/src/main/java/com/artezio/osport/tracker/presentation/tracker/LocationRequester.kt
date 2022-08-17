package com.artezio.osport.tracker.presentation.tracker

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.artezio.osport.tracker.data.trackservice.ServiceLifecycleState
import com.artezio.osport.tracker.data.trackservice.location.LocationRequester
import com.artezio.osport.tracker.presentation.TrackService
import com.google.android.gms.location.*

class LocationRequester(
    private val context: Context
) {

    private val _locationLiveData: MutableLiveData<Location> = MutableLiveData()
    val locationLiveData get() = _locationLiveData

    private val fusedLocationProvider: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = 1000L
        fastestInterval = 1000L
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private lateinit var locationListener: LocationListener
    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            val lastLocation = result.lastLocation
            Log.d("location_delegate", "onLocationResult: $lastLocation")
            _locationLiveData.postValue(lastLocation)
        }
    }

    @SuppressLint("MissingPermission")
    fun subscribeToLocationUpdates() {
        if (TrackService.serviceLifecycleState.value != ServiceLifecycleState.CALIBRATING ||
            TrackService.serviceLifecycleState.value != ServiceLifecycleState.RUNNING
        ) {
            if (LocationRequester.checkIsGmsAvailable(context)) {
                fusedLocationProvider.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } else {
                locationListener = LocationListener { location ->
                    _locationLiveData.postValue(location)
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
            }

        }
    }

    fun unsubscribeToLocationUpdates() {
        if (LocationRequester.checkIsGmsAvailable(context)) {
            fusedLocationProvider.removeLocationUpdates(locationCallback)
        } else {
            locationManager.removeUpdates(locationListener)
        }
    }
}