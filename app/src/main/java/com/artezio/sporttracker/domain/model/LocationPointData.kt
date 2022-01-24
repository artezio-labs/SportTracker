package com.artezio.sporttracker.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "location_data",
    foreignKeys = [
        ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
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