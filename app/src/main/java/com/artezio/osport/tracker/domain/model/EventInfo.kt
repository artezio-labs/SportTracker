package com.artezio.osport.tracker.domain.model

data class EventInfo(
    val title: String,
    val time: String,
    val speed: String,
    val distance: String,
    val tempo: String,
    val steps: String,
    val gpsPoints: String
)