package com.artezio.osport.tracker.di

import android.content.Context
import com.artezio.osport.tracker.data.mappers.DomainToPresentationMapper
import com.artezio.osport.tracker.data.repository.DataStoreRepository
import com.artezio.osport.tracker.data.repository.EventsRepository
import com.artezio.osport.tracker.data.repository.LocationRepository
import com.artezio.osport.tracker.data.repository.PedometerRepository
import com.artezio.osport.tracker.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

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

    @Provides
    fun providesDomainToPresentationMapper() =
        DomainToPresentationMapper()

    @Provides
    fun providesGetStepCountUseCase(repository: PedometerRepository) =
        GetStepCountUseCase(repository)

    @Provides
    fun providesGetTrackingStateUseCase(repository: DataStoreRepository) =
        GetTrackingStateUseCase(repository)

    @Provides
    fun providesSaveTrackingStateUseCase(repository: DataStoreRepository) =
        SaveTrackingStateUseCase(repository)

    @Provides
    fun providesDeleteEventUseCase(repository: EventsRepository) =
        DeleteEventUseCase(repository)

    @Provides
    fun providesGetLastEventUseCase(repository: EventsRepository) =
        GetLastEventUseCase(repository)

    @Provides
    fun provideApplicationContext(@ApplicationContext context: Context) = context

    @Provides
    fun provideGetAllLocationsByIdUseCase(repository: LocationRepository) =
        GetAllLocationsByIdUseCase(repository)
}