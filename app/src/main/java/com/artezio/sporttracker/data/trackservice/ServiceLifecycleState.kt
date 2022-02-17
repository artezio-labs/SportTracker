package com.artezio.sporttracker.data.trackservice

sealed class ServiceLifecycleState {
    object Running : ServiceLifecycleState()
    object Stopped : ServiceLifecycleState()
}
