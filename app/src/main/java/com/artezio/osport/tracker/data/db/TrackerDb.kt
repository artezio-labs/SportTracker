package com.artezio.osport.tracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.artezio.osport.tracker.domain.model.*

@Database(
    entities = [
        TrackData::class,
        LocationPointData::class,
        User::class, Event::class,
        PedometerData::class,
        TrackingStateModel::class,
        PlannedEvent::class
    ],
    version = 5
)
@TypeConverters(Converters::class)
abstract class TrackerDb : RoomDatabase() {
    abstract fun pedometerDao(): PedometerDao
    abstract fun locationDao(): LocationDao
    abstract fun eventsDao(): EventsDao
}