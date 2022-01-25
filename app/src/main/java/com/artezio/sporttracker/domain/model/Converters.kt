package com.artezio.sporttracker.domain.model

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun toStatus(value: Int) = enumValues<EventStatus>()[value]
    @TypeConverter
    fun fromStatus(status: EventStatus) = status.ordinal

}