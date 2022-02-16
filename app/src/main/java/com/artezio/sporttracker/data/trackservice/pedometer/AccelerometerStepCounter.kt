package com.artezio.sporttracker.data.trackservice.pedometer

import android.os.SystemClock
import java.util.*
import kotlin.math.sqrt

class AccelerometerStepCounter {

    private var stepListener: StepListener? = null

    private val newAccelerationDataList: MutableList<AccelerationData> = mutableListOf()
    private val calculatedList: MutableList<AccelerationData> = mutableListOf()

    fun registerStepListener(listener: StepListener) {
        stepListener = listener
    }

    fun addAccelerationData(data: AccelerationData) {
        newAccelerationDataList.add(data)

        if (newAccelerationDataList.size >= 25) {
            handleAccelerationData()
        }
    }

    private fun handleAccelerationData() {
        for (i in newAccelerationDataList.indices) {
            var data = newAccelerationDataList[i]
            data = calculateValueAndTime(data)
            calculatedList.add(data)
        }
        var highPoints = findHighPoints()
        highPoints = removeNearHighPoints(highPoints)
        examineStepTypeAndSendResponse(highPoints)

        calculatedList.clear()
        newAccelerationDataList.clear()
    }

    private fun calculateValueAndTime(data: AccelerationData): AccelerationData {
        val x = data.x
        val y = data.y
        val z = data.z

        val vectorLength = sqrt(x * x + y * y + z * z).toDouble()
        data.value = vectorLength

        val time = data.time
        val timeOffsetToUnix = System.currentTimeMillis() - SystemClock.elapsedRealtime()
        val unixTimeStamp = (time / 1_000_000L) + timeOffsetToUnix
        data.time = unixTimeStamp

        return data
    }

    private fun findHighPoints(): MutableList<AccelerationData> {
        val highPoints = mutableListOf<AccelerationData>()
        val aboveWalkingThresholdList = mutableListOf<AccelerationData>()
        var wasAboveThreshold = true

        for (i in calculatedList.indices) {
            val calculatedDataSet = calculatedList[i]
            if (calculatedDataSet.value > WALKING_THRESHOLD) {
                aboveWalkingThresholdList.add(calculatedDataSet)
                wasAboveThreshold = true
            } else {
                if (wasAboveThreshold && aboveWalkingThresholdList.size > 0) {
                    Collections.sort(aboveWalkingThresholdList, AccelerationDataSorter())
                    highPoints.add(aboveWalkingThresholdList[aboveWalkingThresholdList.size - 1])
                    aboveWalkingThresholdList.clear()
                }
                wasAboveThreshold = false
            }
        }
        return highPoints
    }

    private fun removeNearHighPoints(data: MutableList<AccelerationData>): MutableList<AccelerationData> {
        val wrongHighPointIndexes = mutableListOf<Int>()
        for (i in data.indices) {
            if (data[i + 1].time - data[i].time < 400) {
                if (data[i + 1].value < data[i].value) {
                    wrongHighPointIndexes.add(i + 1)
                } else {
                    wrongHighPointIndexes.add(i)
                }
            }
        }
        for (i in wrongHighPointIndexes.indices.reversed()) {
            data.removeAt(i)
        }
        return data
    }

    private fun examineStepTypeAndSendResponse(data: MutableList<AccelerationData>) {
        for (i in data.indices) {
            val highPoint = data[i]
            when {
                highPoint.value > RUNNING_THRESHOLD -> {
                    stepListener?.step(highPoint, StepType.RUNNING)
                }
                highPoint.value > JOGGING_THRESHOLD -> {
                    stepListener?.step(highPoint, StepType.JOGGING)
                }
                else -> {
                    stepListener?.step(highPoint, StepType.WALKING)
                }
            }
        }
    }

    class AccelerationDataSorter: Comparator<AccelerationData> {

        override fun compare(data1: AccelerationData?, data2: AccelerationData?): Int {
            var returnValue = 0
            if (data1!!.value < data2!!.value) {
                returnValue = -1
            } else if (data1.value > data2.value) {
                returnValue = 1
            }
            return returnValue
        }

    }

    companion object {
        private const val WALKING_THRESHOLD = 17
        private const val JOGGING_THRESHOLD = 24
        private const val RUNNING_THRESHOLD = 30
    }

    interface StepListener {
        fun step(data: AccelerationData, stepType: StepType)
    }

}