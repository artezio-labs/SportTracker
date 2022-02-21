package com.artezio.osport.tracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "pedometer_data",
)
data class PedometerData(
    val stepCount: Int,
    val time: Long,
    val eventId: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
