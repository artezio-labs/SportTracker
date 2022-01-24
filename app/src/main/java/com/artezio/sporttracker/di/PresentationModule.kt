package com.artezio.sporttracker.di

import com.artezio.sporttracker.data.repository.LocationRepository
import com.artezio.sporttracker.data.repository.PedometerRepository
import com.artezio.sporttracker.domain.usecases.InsertLocationDataUseCase
import com.artezio.sporttracker.domain.usecases.InsertPedometerDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PresentationModule {

    @Provides
    fun provideInsertLocationDataUseCase(repository: LocationRepository) =
        InsertLocationDataUseCase(repository)
    @Provides
    fun providesInsertPedometerDataUseCase(repository: PedometerRepository) =
        InsertPedometerDataUseCase(repository)

}