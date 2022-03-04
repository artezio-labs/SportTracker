package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.LocationRepository
import com.artezio.osport.tracker.domain.model.LocationPointData
import javax.inject.Inject

class InsertLocationDataUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend fun execute(locationPointData: LocationPointData) =
        repository.addLocationPointData(locationPointData)
}