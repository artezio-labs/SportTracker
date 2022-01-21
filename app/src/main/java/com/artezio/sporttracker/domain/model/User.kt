package com.artezio.sporttracker.domain.model

import androidx.room.*

@Entity(tableName = "users")
data class User(
    val name: String,
    val age: String,
) {
    @PrimaryKey(autoGenerate = true)
    var userId: Int = 0
}
