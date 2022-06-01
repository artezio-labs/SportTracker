package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.LocationRepository
import com.artezio.osport.tracker.util.calculateRouteDistance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveDistanceUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    fun execute(id: Long): Flow<Double> {
        return repository.getLocationsByEventId(id).map { locations ->
            calculateRouteDistance(locations)
        }
    }
}