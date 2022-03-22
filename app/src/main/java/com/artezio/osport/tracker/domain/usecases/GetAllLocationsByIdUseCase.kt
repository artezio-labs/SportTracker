package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.LocationRepository
import javax.inject.Inject

class GetAllLocationsByIdUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend fun execute(id: Long) = repository.getAllLocationsById(id)
}