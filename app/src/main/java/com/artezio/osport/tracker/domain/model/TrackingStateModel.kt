package com.artezio.osport.tracker.domain.model

import androidx.room.Entity


@Entity(tableName = "tracking_state")
data class TrackingStateModel(
    val timerValue: Double,
    val speedValue: Double,
    val distanceValue: Double,
    val tempoValue: Double,
    val stepsValue: Int,
    val gpsPointsValue: Int,
) {
    companion object {
        fun empty(): TrackingStateModel =
            TrackingStateModel(
                timerValue = 0.0,
                speedValue = 0.0,
                distanceValue = 0.0,
                tempoValue = 0.0,
                stepsValue = 0,
                gpsPointsValue = 0
            )
    }
}