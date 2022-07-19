package com.artezio.osport.tracker.util

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getString(@StringRes id: Int): String =
        context.getString(id)

    fun getStringArray(id: Int): Array<String> =
        context.resources.getStringArray(id)
}