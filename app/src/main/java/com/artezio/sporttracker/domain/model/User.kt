package com.artezio.sporttracker.domain.model

import androidx.room.*
import java.util.*

@Entity(tableName = "users")
data class User(
    val name: String,
    val age: String,
) {
    @PrimaryKey
    var userId: String = UUID.randomUUID().toString()
}
