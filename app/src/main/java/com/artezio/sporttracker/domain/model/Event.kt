package com.artezio.sporttracker.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("userId"),
            childColumns = arrayOf("sportsmanId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Event(
    val name: String,
    val startDate: Long,
    val endDate: Long,
    val sportsmanId: Long
) {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
}
