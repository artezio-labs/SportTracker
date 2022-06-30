package com.artezio.osport.tracker.data.trackservice.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager

class GpsLocationRequester(
    context: Context
) : LocationRequester() {
    private var locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private lateinit var locationListener: LocationListener

    @SuppressLint("MissingPermission")
    override fun subscribeToLocationUpdates(onLocationChanged: (Location) -> Unit) {
        locationListener = LocationListener { location ->
            onLocationChanged.invoke(location)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0F, locationListener)
    }

    override fun unsubscribeToLocationUpdates() {
        if (::locationListener.isInitialized) {
            locationManager.removeUpdates(locationListener)
        }
    }
}