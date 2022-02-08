package com.artezio.sporttracker.di

import com.artezio.sporttracker.data.repository.EventsRepository
import com.artezio.sporttracker.data.repository.LocationRepository
import com.artezio.sporttracker.data.repository.PedometerRepository
import com.artezio.sporttracker.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ViewModelComponent::class)
object PresentationModule {

    @Provides
    fun provideInsertLocationDataUseCase(repository: LocationRepository) =
        InsertLocationDataUseCase(repository)

    @Provides
    fun provideInsertPedometerDataUseCase(repository: PedometerRepository) =
        InsertPedometerDataUseCase(repository)

    @Provides
    fun provideInsertEventUseCase(repository: EventsRepository) =
        InsertEventUseCase(repository)

    @Provides
    fun provideGetAllEventsWithDataUseCase(repository: EventsRepository) =
        GetAllEventsWithDataUseCase(repository)

    @Provides
    fun providesGetEventByIdUseCase(repository: EventsRepository) =
        GetEventByIdUseCase(repository)

    @Provides
    fun providesUpdateEventUseCase(repository: EventsRepository) =
        UpdateEventUseCase(repository)

    @Provides
    fun providesGetEventWithDataByIdUseCase(repository: EventsRepository) =
        GetEventWithDataByIdUseCase(repository)

    @Provides
    fun providesGetAllLocationDataUseCase(repository: LocationRepository) =
        GetAllLocationDataUseCase(repository)

    @Provides
    fun providesGetLastEventIdUseCase(repository: EventsRepository) =
        GetLastEventIdUseCase(repository)

    @Provides
    fun providesGetLocationsByEventIdUseCase(repository: LocationRepository) =
        GetLocationsByEventIdUseCase(repository)
}