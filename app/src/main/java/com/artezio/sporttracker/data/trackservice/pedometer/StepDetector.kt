package com.artezio.sporttracker.data.trackservice.pedometer

import kotlin.math.min
import kotlin.math.sqrt

// Класс, на тот случай, когда нет сенсора в телефоне
class StepDetector(
    private val listener: StepListener
) {
    private var accelRingCounter = 0
    private val accelRingX = FloatArray(ACCEL_RING_SIZE)
    private val accelRingY = FloatArray(ACCEL_RING_SIZE)
    private val accelRingZ = FloatArray(ACCEL_RING_SIZE)
    private var velRingCounter = 0
    private val velRing = FloatArray(VEL_RING_SIZE)
    private var lastStepTimeNs: Long = 0
    private var oldVelocityEstimate = 0f

    internal fun updateAccel(timeNs: Long, x: Float, y: Float, z: Float) {
        val currentAccel = FloatArray(3)
        currentAccel[0] = x
        currentAccel[1] = y
        currentAccel[2] = z


        accelRingCounter++
        accelRingX[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[0]
        accelRingY[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[1]
        accelRingZ[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[2]

        val worldZ = FloatArray(3)
        worldZ[0] = sum(accelRingX) / min(accelRingCounter, ACCEL_RING_SIZE)
        worldZ[1] = sum(accelRingY) / min(accelRingCounter, ACCEL_RING_SIZE)
        worldZ[2] = sum(accelRingZ) / min(accelRingCounter, ACCEL_RING_SIZE)

        val normalizationFactor = norm(worldZ)

        worldZ[0] = worldZ[0] / normalizationFactor
        worldZ[1] = worldZ[1] / normalizationFactor
        worldZ[2] = worldZ[2] / normalizationFactor

        val currentZ = point(worldZ, currentAccel) - normalizationFactor
        velRingCounter++
        velRing[velRingCounter % VEL_RING_SIZE] = currentZ

        val velocityEstimate = sum(velRing)

        if (velocityEstimate > STEP_THRESHOLD && oldVelocityEstimate <= STEP_THRESHOLD
            && timeNs - lastStepTimeNs > STEP_DELAY_NS
        ) {
            listener.step(timeNs)
            lastStepTimeNs = timeNs
        }
        oldVelocityEstimate = velocityEstimate

    }

    private fun sum(array: FloatArray): Float {
        var sum = 0f
        for (value in array) {
            sum += value
        }
        return sum
    }

    private fun point(first: FloatArray, second: FloatArray): Float {
        return first[0] * second[0] + first[1] * second[1] + first[2] * second[2]
    }

    private fun norm(array: FloatArray): Float {
        var norm = 0f
        for (value in array) {
            norm += value * value
        }
        return sqrt(norm.toDouble()).toFloat()
    }

    interface StepListener {
        fun step(timeNs: Long)
    }

    companion object {
        private const val ACCEL_RING_SIZE = 50
        private const val VEL_RING_SIZE = 10
        private const val STEP_THRESHOLD = 50f
        private const val STEP_DELAY_NS = 250000000

    }
}