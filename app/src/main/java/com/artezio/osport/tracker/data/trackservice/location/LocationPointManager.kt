package com.artezio.osport.tracker.data.trackservice.location

import android.content.Context
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

class LocationPointManager(
    private val context: Context
) {

    private val locationRequest = LocationRequest.create().apply {
        interval = LOCATION_UPDATE_INTERVAL
        fastestInterval = LOCATION_FASTEST_INTERVAL
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    fun checkSettingsAndStartLocationUpdates() {
        val request = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest).build()
        val settingsClient = LocationServices.getSettingsClient(context)

        val locationSettingsResponseTask = settingsClient.checkLocationSettings(request)

        locationSettingsResponseTask.addOnSuccessListener {
            startLocationUpdates()
        }

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {

            }
        }
    }

    private fun startLocationUpdates() {}

    companion object {
        const val LOCATION_UPDATE_INTERVAL = 400L
        const val LOCATION_FASTEST_INTERVAL = 350L
    }

}