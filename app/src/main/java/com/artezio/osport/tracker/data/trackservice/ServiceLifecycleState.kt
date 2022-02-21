package com.artezio.osport.tracker.data.trackservice

sealed class ServiceLifecycleState {
    object Running : ServiceLifecycleState()
    object Stopped : ServiceLifecycleState()
}
