package com.artezio.osport.tracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planned_events")
data class PlannedEvent(
    val name: String,
    val startDate: Long,
    val duration: Int = 120,
    val calibrationTime: Int = 1
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    override fun toString(): String {
        return "PlannedEvent(id='$id', name='$name', startDate=$startDate, duration=$duration)"
    }
}