package com.artezio.sporttracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.artezio.sporttracker.domain.model.LocationPointData
import com.artezio.sporttracker.domain.model.TrackData

@Database(entities = [TrackData::class, LocationPointData::class], version = 1)
abstract class TrackerDb : RoomDatabase() {
    abstract fun trackerDao(): TrackerDao
}