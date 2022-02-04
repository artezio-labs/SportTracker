package com.artezio.sporttracker.presentation.tracker

import androidx.lifecycle.ViewModel
import com.artezio.sporttracker.domain.usecases.GetAllLocationDataUseCase
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val getAllLocationDataUseCase: GetAllLocationDataUseCase
): ViewModel() {

    val locationDataFlow: Flow<List<LatLng>>
        get() = getAllLocationDataUseCase.execute()


}