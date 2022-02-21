package com.artezio.osport.tracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "location_data",
)
data class LocationPointData(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val accuracy: Float,
    val speed: Float,
    val time: Long,
    val batteryLevel: Int,
    val eventId: Long,
) {
    @PrimaryKey(autoGenerate = true)
    var pointId: Long = 0
}