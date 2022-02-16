package com.artezio.sporttracker.domain.model

import androidx.room.*
import java.util.*

@Entity(tableName = "users")
data class User(
    val name: String,
    val age: String,
) {
    @PrimaryKey(autoGenerate = true)
    var userId: Long = 0
}

data class UserWithEvents(
    @Embedded val user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "sportsmanId"
    )
    val events: List<Event>
)
