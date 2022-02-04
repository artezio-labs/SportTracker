package com.artezio.sporttracker.domain.usecases

import com.artezio.sporttracker.data.repository.LocationRepository
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