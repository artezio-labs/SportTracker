package com.artezio.sporttracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.artezio.sporttracker.domain.model.Event
import com.artezio.sporttracker.domain.model.LocationPointData
import com.artezio.sporttracker.domain.model.TrackData
import com.artezio.sporttracker.domain.model.User

@Database(entities = [TrackData::class, LocationPointData::class, User::class, Event::class], version = 2)
abstract class TrackerDb : RoomDatabase() {
    abstract fun trackerDao(): TrackerDao
}