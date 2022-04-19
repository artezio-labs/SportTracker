package com.artezio.osport.tracker.util

import com.artezio.osport.tracker.domain.model.LocationPointData
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.PuckBearingSource
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.locationcomponent.location2

object MapUtils {

    private lateinit var mapView: MapView

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    fun initMap(map: MapView, lastLocation: LocationPointData? = null) {
        mapView = map
        mapView.apply {
            getMapboxMap().apply {
                loadStyleUri(Style.SATELLITE)
                setCamera(
                    CameraOptions.Builder()
                        .zoom(14.0)
                        .build()
                )
            }
            location2.puckBearingSource = PuckBearingSource.HEADING
            initLocationComponent()
            setupGesturesListener()
        }

    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun onCameraTrackingDismissed() {
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        mapView.location.apply {
            updateSettings {
                enabled = true
                pulsingEnabled = false

            }
            addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        }
    }

    fun onDestroy() {
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }
}

