package com.artezio.osport.tracker.data.preferences

import kotlinx.coroutines.flow.Flow

interface PreferencesManager<T> {
    suspend fun save(tag: String, value: T)
    suspend fun get(flag: Boolean): Flow<T>
}