package com.artezio.sporttracker.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

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
    val eventId: Long,
) {
    @PrimaryKey(autoGenerate = true)
    var pointId: Long = 0
}