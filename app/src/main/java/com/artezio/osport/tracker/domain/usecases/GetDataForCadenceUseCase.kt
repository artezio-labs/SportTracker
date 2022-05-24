package com.artezio.osport.tracker.domain.usecases

import android.util.Log
import com.artezio.osport.tracker.data.repository.PedometerRepository
import com.artezio.osport.tracker.domain.model.PedometerData
import com.artezio.osport.tracker.util.CADENCE_STEP_FILTER_VALUE
import com.artezio.osport.tracker.util.MINUTE
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDataForCadenceUseCase @Inject constructor(
    private val repository: PedometerRepository
) {
    fun execute() = repository.getAllPedometerDataFlow().map { data ->
        if (data.isEmpty()) {
            emptyList()
        } else {
            Log.d("CADENCE", "pedometer data: $data")
            val result = mutableListOf<PedometerData>()
            val dataInLastMinute = data?.filter { (data.last().time - it.time) <= MINUTE }
            Log.d(
                "CADENCE",
                "pedometer data after filter: $dataInLastMinute \n data size: ${dataInLastMinute.size}"
            )
            Log.d(
                "CADENCE",
                "time interval: ${dataInLastMinute.last().time - dataInLastMinute.first().time}"
            )
            Log.d("CADENCE", "steps per minute: ${dataInLastMinute.size}")

            for (i in 0 until dataInLastMinute.size - 1) {
                if ((dataInLastMinute[i + 1].time - dataInLastMinute[i].time) <= CADENCE_STEP_FILTER_VALUE) {
                    if (i == data.size - 1) {
                        result.add(dataInLastMinute[i])
                        result.add(dataInLastMinute[dataInLastMinute.size - 1])
                    } else {
                        result.add(dataInLastMinute[i])
                    }
                }
            }

            Log.d("CADENCE", "Cadence result: $result")
            result
        }
    }
}