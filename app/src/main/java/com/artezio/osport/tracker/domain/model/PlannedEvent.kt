package com.artezio.osport.tracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planned_events")
data class PlannedEvent(
    val name: String,
    val startDate: Long,
    val endDate: Long,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
}