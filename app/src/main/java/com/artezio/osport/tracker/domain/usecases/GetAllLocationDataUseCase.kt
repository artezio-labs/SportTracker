package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.LocationRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllLocationDataUseCase @Inject constructor(
    private val repository: LocationRepository
) {

    fun execute() = repository.getAllLocationData().map { locationPointData ->
        locationPointData.map {
            LatLng(it.latitude, it.longitude)
        }
    }
}