package com.artezio.osport.tracker.presentation.statistics

import com.artezio.osport.tracker.domain.usecases.GetEventWithDataByIdUseCase
import com.artezio.osport.tracker.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getEventWithDataByIdUseCase: GetEventWithDataByIdUseCase
): BaseViewModel(){

    fun getEventWithDataUseCase(id: Long) =
        getEventWithDataByIdUseCase.execute(id)

}