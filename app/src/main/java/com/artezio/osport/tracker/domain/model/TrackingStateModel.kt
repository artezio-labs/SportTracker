package com.artezio.osport.tracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracking_state")
data class TrackingStateModel(
    val timerValue: Double,
    val speedValue: Double,
    val distanceValue: Double,
    val tempoValue: Double,
    val stepsValue: Int,
    val gpsPointsValue: Int,
    val eventId: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    companion object {
        fun empty(): TrackingStateModel =
            TrackingStateModel(
                timerValue = 0.0,
                speedValue = 0.0,
                distanceValue = 0.0,
                tempoValue = 0.0,
                stepsValue = 0,
                gpsPointsValue = 0,
                eventId = -1L
            )
    }
}