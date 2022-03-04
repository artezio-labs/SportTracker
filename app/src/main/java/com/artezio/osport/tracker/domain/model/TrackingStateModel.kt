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
)