package com.artezio.osport.tracker.domain.model

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun toStatus(value: Int) = enumValues<EventStatus>()[value]
    @TypeConverter
    fun fromStatus(status: EventStatus) = status.ordinal

}