package com.artezio.sporttracker.di

import android.content.Context
import androidx.room.Room
import com.artezio.sporttracker.data.db.LocationDao
import com.artezio.sporttracker.data.db.PedometerDao
import com.artezio.sporttracker.data.db.TrackerDb
import com.artezio.sporttracker.data.repository.LocationRepository
import com.artezio.sporttracker.data.repository.PedometerRepository
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
    fun providePedometerRepository(dao: PedometerDao) = PedometerRepository(dao)

    @Provides
    fun provideLocationUseCase(dao: LocationDao) = LocationRepository(dao)


}