package com.artezio.sporttracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.artezio.sporttracker.domain.model.*

@Database(entities = [TrackData::class, LocationPointData::class, User::class, Event::class, PedometerData::class], version = 1)
abstract class TrackerDb : RoomDatabase() {
    abstract fun pedometerDao(): PedometerDao
    abstract fun locationDao(): LocationDao
}