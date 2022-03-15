package com.artezio.osport.tracker.data.permissions

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PermissionResult(
    val permission: String,
    val state: PermissionState
): Parcelable