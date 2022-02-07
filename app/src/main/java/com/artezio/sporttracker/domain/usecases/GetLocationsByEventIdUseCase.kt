package com.artezio.sporttracker.domain.usecases

import com.artezio.sporttracker.data.repository.LocationRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.util.concurrent.Flow
import javax.inject.Inject

class GetLocationsByEventIdUseCase @Inject constructor(
    private val repository: LocationRepository
) {

    fun execute(id: Long) = repository.getLocationsByEventId(id).map { data ->
        data
            .map { LatLng(it.latitude, it.longitude) }
    }
}