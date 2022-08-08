package com.artezio.osport.tracker.di

import android.content.Context
import androidx.room.Room
import com.artezio.osport.tracker.data.db.EventsDao
import com.artezio.osport.tracker.data.db.LocationDao
import com.artezio.osport.tracker.data.db.PedometerDao
import com.artezio.osport.tracker.data.db.TrackerDb
import com.artezio.osport.tracker.data.preferences.SettingsPreferencesManager
import com.artezio.osport.tracker.data.repository.EventsRepository
import com.artezio.osport.tracker.data.repository.LocationRepository
import com.artezio.osport.tracker.data.repository.PedometerRepository
import com.artezio.osport.tracker.data.trackservice.location.GpsLocationRequester
import com.artezio.osport.tracker.domain.usecases.UpdateEventNameUseCase
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
    fun providesGpsLocationRequester(@ApplicationContext context: Context) =
        GpsLocationRequester(context)

    @Singleton
    @Provides
    fun providesSettingsPreferencesManager(@ApplicationContext context: Context) =
        SettingsPreferencesManager(context)

}