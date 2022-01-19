package com.artezio.sporttracker.di

import android.content.Context
import androidx.room.Room
import com.artezio.sporttracker.data.db.TrackerDb
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
        ).build()

    @Provides
    fun provideTrackerDao(db: TrackerDb) =
        db.trackerDao()

}