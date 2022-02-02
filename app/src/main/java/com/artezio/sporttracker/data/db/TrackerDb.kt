package com.artezio.sporttracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.artezio.sporttracker.domain.model.*

@Database(
    entities = [TrackData::class, LocationPointData::class, User::class, Event::class, PedometerData::class],
    version = 5
)
@TypeConverters(Converters::class)
abstract class TrackerDb : RoomDatabase() {
    abstract fun pedometerDao(): PedometerDao
    abstract fun locationDao(): LocationDao
    abstract fun eventsDao(): EventsDao
}