package com.artezio.osport.tracker.di

import android.content.Context
import com.artezio.osport.tracker.data.mappers.DomainToPresentationMapper
import com.artezio.osport.tracker.data.repository.EventsRepository
import com.artezio.osport.tracker.data.repository.LocationRepository
import com.artezio.osport.tracker.data.repository.PedometerRepository
import com.artezio.osport.tracker.domain.usecases.*
import com.artezio.osport.tracker.presentation.tracker.AccuracyFactory
import com.artezio.osport.tracker.util.ResourceProvider
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
    fun providesGetLocationsByEventIdUseCase(
        repository: LocationRepository,
        accuracyFactory: AccuracyFactory
    ) =
        GetLocationsByEventIdUseCase(repository, accuracyFactory)

    @Provides
    fun providesDomainToPresentationMapper() =
        DomainToPresentationMapper()

    @Provides
    fun providesGetStepCountUseCase(repository: PedometerRepository) =
        GetStepCountUseCase(repository)

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

    @Provides
    fun providesAccuracyFactory() = AccuracyFactory()

    @Provides
    fun providesGetEventInfoUseCase(
        eventsRepository: EventsRepository,
        locationsRepository: LocationRepository,
        pedometerRepository: PedometerRepository,
        resourceProvider: ResourceProvider
    ) = GetEventInfoUseCase(
        eventsRepository,
        locationsRepository,
        pedometerRepository,
        resourceProvider
    )
}