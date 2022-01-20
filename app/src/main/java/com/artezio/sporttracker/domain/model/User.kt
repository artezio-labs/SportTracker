package com.artezio.sporttracker.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "users"
)
data class User(
    val name: String,
    val email: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
