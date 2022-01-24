package com.artezio.sporttracker.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

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
