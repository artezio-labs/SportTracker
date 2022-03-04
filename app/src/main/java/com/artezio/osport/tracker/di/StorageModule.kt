package com.artezio.osport.tracker.di

import android.content.Context
import androidx.room.Room
import com.artezio.osport.tracker.data.db.EventsDao
import com.artezio.osport.tracker.data.db.LocationDao
import com.artezio.osport.tracker.data.db.PedometerDao
import com.artezio.osport.tracker.data.db.TrackerDb
import com.artezio.osport.tracker.data.prefs.DataStore
import com.artezio.osport.tracker.data.prefs.PrefsManager
import com.artezio.osport.tracker.data.repository.DataStoreRepository
import com.artezio.osport.tracker.data.repository.EventsRepository
import com.artezio.osport.tracker.data.repository.LocationRepository
import com.artezio.osport.tracker.data.repository.PedometerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideAppDataBase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            TrackerDb::class.java,
            "TrackerDb"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providePedometerDao(db: TrackerDb) =
        db.pedometerDao()

    @Provides
    fun provideLocationDao(db: TrackerDb) =
        db.locationDao()

    @Provides
    fun provideEventsDao(db: TrackerDb) =
        db.eventsDao()

    @Provides
    fun providePedometerRepository(dao: PedometerDao) = PedometerRepository(dao)

    @Provides
    fun provideEventsRepository(dao: EventsDao) = EventsRepository(dao)

    @Provides
    fun provideLocationRepository(dao: LocationDao) = LocationRepository(dao)

    @Provides
    fun providesPrefsManager(@ApplicationContext context: Context) =
        PrefsManager(context)

    @Provides
    @Singleton
    fun providesDataStore(@ApplicationContext context: Context) =
        DataStore(context)

    @Provides
    fun providesDataStoreRepository(dataStore: DataStore) =
        DataStoreRepository(dataStore)
}