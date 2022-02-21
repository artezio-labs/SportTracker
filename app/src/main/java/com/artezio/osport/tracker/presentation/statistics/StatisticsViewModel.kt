package com.artezio.osport.tracker.presentation.statistics

import androidx.lifecycle.ViewModel
import com.artezio.osport.tracker.domain.usecases.GetEventWithDataByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getEventWithDataByIdUseCase: GetEventWithDataByIdUseCase
): ViewModel(){

    fun getEventWithDataUseCase(id: Long) =
        getEventWithDataByIdUseCase.execute(id)

}