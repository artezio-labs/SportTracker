package com.artezio.sporttracker.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "events"
)
data class Event(
    val name: String,
    @ColumnInfo(name = "start_date")
    val startDate: Long,
    @ColumnInfo(name = "end_date")
    val endDate: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
