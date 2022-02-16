package com.artezio.sporttracker.domain.usecases

import com.artezio.sporttracker.data.repository.LocationRepository
import com.artezio.sporttracker.domain.model.LocationPointData
import javax.inject.Inject


class InsertLocationDataUseCase @Inject constructor(
    private val repository: LocationRepository
) {

    suspend fun execute(locationPointData: LocationPointData) =
        repository.addLocationPointData(locationPointData)

}