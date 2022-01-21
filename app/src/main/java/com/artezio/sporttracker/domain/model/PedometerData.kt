package com.artezio.sporttracker.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "pedometer_data",
    foreignKeys = [
        ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PedometerData(
    val stepCount: Int,
    val time: Long,
    val eventId: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
