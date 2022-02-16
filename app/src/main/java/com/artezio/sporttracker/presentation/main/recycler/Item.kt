package com.artezio.sporttracker.presentation.main.recycler

sealed class Item {
    data class EventTypeHeader(val title: String): Item()
    data class Event(val id: Long, val eventName: String, val startDate: Long, val endDate: Long? = null): Item()
}