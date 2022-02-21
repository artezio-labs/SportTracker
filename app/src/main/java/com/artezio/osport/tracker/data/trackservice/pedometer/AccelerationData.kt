package com.artezio.osport.tracker.data.trackservice.pedometer

data class AccelerationData(
    var value: Double = 0.0,
    var x: Float = 0.0F,
    var y: Float = 0.0F,
    var z: Float = 0.0F,
    var time: Long = 0L
)