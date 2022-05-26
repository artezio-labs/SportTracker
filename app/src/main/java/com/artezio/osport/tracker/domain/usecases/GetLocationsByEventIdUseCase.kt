package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.mappers.LocationToPointMapper
import com.artezio.osport.tracker.data.repository.LocationRepository
import com.artezio.osport.tracker.presentation.tracker.AccuracyFactory
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLocationsByEventIdUseCase @Inject constructor(
    private val repository: LocationRepository,
    private val accuracyFactory: AccuracyFactory,
    private val mapper: LocationToPointMapper
) {
    fun executeWithAccuracy(id: Long) = repository.getLocationsByEventId(id).map { data ->
        data.map { locationPointData ->
            Pair(
                locationPointData,
                accuracyFactory.calculateAccuracy(locationPointData.accuracy)
            )
        }
    }

    fun execute(id: Long) = repository.getLocationsByEventId(id).map { points ->
        points.map { point ->
            mapper.map(point)
        }
    }
}