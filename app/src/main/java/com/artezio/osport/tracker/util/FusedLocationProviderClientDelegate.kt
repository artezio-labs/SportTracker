package com.artezio.osport.tracker.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.artezio.osport.tracker.presentation.tracker.LocationRequester
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FusedLocationProviderClientDelegate(fragment: Fragment) :
    ReadOnlyProperty<Fragment, LocationRequester?> {

    private lateinit var locationRequester: LocationRequester

    init {
        fragment.lifecycle.addObserver(object: DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                fragment.requireActivity().let {
                    locationRequester = LocationRequester(fragment.requireContext())
                }
                if (this@FusedLocationProviderClientDelegate::locationRequester.isInitialized) {
                    locationRequester.subscribeToLocationUpdates()
                }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                if (this@FusedLocationProviderClientDelegate::locationRequester.isInitialized) {
                    locationRequester.unsubscribeToLocationUpdates()
                }
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): LocationRequester? {
        return if (this@FusedLocationProviderClientDelegate::locationRequester.isInitialized) {
            locationRequester
        } else { null }
    }
}