package com.artezio.osport.tracker.domain.usecases

import com.artezio.osport.tracker.data.repository.PedometerRepository
import com.artezio.osport.tracker.domain.model.PedometerData
import javax.inject.Inject

class InsertPedometerDataUseCase @Inject constructor(
    private val repository: PedometerRepository
) {

    suspend fun execute(data: PedometerData) = repository.addPedometerData(data)

}