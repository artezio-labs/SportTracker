package com.artezio.sporttracker.domain.usecases

import com.artezio.sporttracker.data.repository.PedometerRepository
import com.artezio.sporttracker.domain.model.PedometerData
import javax.inject.Inject

class InsertPedometerDataUseCase @Inject constructor(
    private val repository: PedometerRepository
) {

    suspend fun execute(data: PedometerData) = repository.addPedometerData(data)

}