package com.artezio.osport.tracker.data.trackservice.location

import android.content.Context
import android.location.Location
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

abstract class LocationRequester {
    abstract fun subscribeToLocationUpdates(onLocationChanged: (Location) -> Unit)
    abstract fun unsubscribeToLocationUpdates()

    companion object {
        fun checkIsGmsAvailable(context: Context): Boolean {
            val googleApiAvailability = GoogleApiAvailability.getInstance()
            return googleApiAvailability.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
        }
    }
}