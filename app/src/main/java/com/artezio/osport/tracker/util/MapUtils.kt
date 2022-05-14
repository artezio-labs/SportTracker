package com.artezio.osport.tracker.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.domain.model.LocationPointData
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.PuckBearingSource
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.locationcomponent.location2

object MapUtils {

    private const val LINE_SOURCE = "line-source"
    private const val LINE_LAYER = "line-layer"

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

    fun drawRoute(context: Context, map: MapView, locations: List<Point>) {
        with(map.getMapboxMap()) {
            loadMapStyle(context, this, locations)
            setMapCamera(this, locations)
        }
    }

    private fun loadMapStyle(context: Context, map: MapboxMap, locations: List<Point>) {
        map.loadStyle(
            style(styleUri = Style.SATELLITE) {
                if (locations.size > 1) {
                    +geoJsonSource(LINE_SOURCE) {
                        featureCollection(
                            FeatureCollection.fromFeatures(
                                arrayOf(
                                    Feature.fromGeometry(
                                        LineString.fromLngLats(
                                            locations
                                        )
                                    )
                                )
                            )
                        )
                    }
                    +lineLayer(LINE_LAYER, LINE_SOURCE) {
                        lineCap(LineCap.ROUND)
                        lineJoin(LineJoin.ROUND)
                        lineOpacity(0.5)
                        lineWidth(5.0)
                        lineColor(
                            ContextCompat.getColor(
                                context,
                                R.color.app_theme_color
                            )
                        )
                    }
                }
            }
        )

    }

    private fun setMapCamera(map: MapboxMap, locations: List<Point>) {
        map.setCamera(
            CameraOptions.Builder().center(
                Point.fromLngLat(
                    locations.last().longitude(),
                    locations.last().latitude(),
                    locations.last().altitude()
                )
            ).zoom(15.5)
                .build()
        )
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

