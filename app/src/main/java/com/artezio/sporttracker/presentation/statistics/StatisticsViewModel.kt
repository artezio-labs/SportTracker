package com.artezio.sporttracker.presentation.statistics

import androidx.lifecycle.ViewModel
import com.artezio.sporttracker.domain.model.EventWithData
import com.artezio.sporttracker.domain.usecases.GetEventByIdUseCase
import com.artezio.sporttracker.domain.usecases.GetEventWithDataByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getEventWithDataByIdUseCase: GetEventWithDataByIdUseCase
): ViewModel(){

    fun getEventWithDataUseCase(id: Long) =
        getEventWithDataByIdUseCase.execute(id)

}