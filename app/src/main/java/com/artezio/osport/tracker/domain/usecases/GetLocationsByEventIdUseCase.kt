package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.LocationRepository
import com.artezio.osport.tracker.presentation.tracker.Accuracy
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLocationsByEventIdUseCase @Inject constructor(
    private val repository: LocationRepository
) {

    fun execute(id: Long) = repository.getLocationsByEventId(id).map { data ->
        data.map { locationPointData ->
            Pair(
                locationPointData,
                when {
                    (0F..5F).contains(locationPointData.accuracy) -> {
                        Accuracy.GOOD
                    }
                    (5.1F..15F).contains(locationPointData.accuracy) -> {
                        Accuracy.MEDIUM
                    }
                    else -> {
                        Accuracy.BAD
                    }
                }
            )
        }
    }

    fun executeWithoutMap(id: Long) = repository.getLocationsByEventId(id)
}