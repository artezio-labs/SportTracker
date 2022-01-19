package com.artezio.sporttracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_data")
data class LocationPointData(
    val id: String,
    val userId: String,
    val eventId: String,
    val time: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val accuracy: Double,
    val speed: Double,
    val bearing: Double,
    val source: String
) {
    @PrimaryKey(autoGenerate = true)
    var pointId: Long = 0
}