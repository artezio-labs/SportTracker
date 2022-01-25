package com.artezio.sporttracker.data.trackservice.pedometer

data class AccelerationData(
    var value: Double,
    val x: Float,
    val y: Float,
    val z: Float,
    var time: Long
)