package com.artezio.osport.tracker.presentation.main.recycler

sealed class Item {
    data class Event(val id: Long, val eventName: String, val startDate: Long, val endDate: Long? = null): Item()
    data class PlannedEvent(val id: Long, val eventName: String, val startDate: Long, val duration: Int, val calibrationTime: Int): Item()
}