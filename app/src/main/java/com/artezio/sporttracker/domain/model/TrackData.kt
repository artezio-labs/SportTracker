package com.artezio.sporttracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_data")
data class TrackData(
    val userId: String,
    val eventId: String,
    val updateTime: Long,
    val sentPoints: Int,
    val notSentPoints: Int,
    val state: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}