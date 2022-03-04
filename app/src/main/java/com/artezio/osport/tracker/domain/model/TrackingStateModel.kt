package com.artezio.osport.tracker.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackingStateModel(
    val timerValue: Double,
    val speedValue: Double,
    val distanceValue: Double,
    val tempoValue: Double,
    val stepsValue: Int,
    val gpsPointsValue: Int
): Parcelable