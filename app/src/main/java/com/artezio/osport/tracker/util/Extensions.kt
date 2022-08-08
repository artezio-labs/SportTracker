package com.artezio.osport.tracker.util

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import java.util.regex.Pattern


fun <T> LiveData<T>.observeNonNull(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner) {
        it?.let(observer)
    }
}

fun Long.ifZero(block: () -> Long): Long {
    return if (this == 0L) {
        block.invoke()
    } else this
}

fun String.isBlankOrEmpty(): Boolean {
    return this.isBlank() || this.isEmpty()
}

fun String.isNotBlankOrEmpty(): Boolean {
    return !this.isBlankOrEmpty()
}

fun String.matches(patternString: String): Boolean {
    val pattern = Pattern.compile(patternString)
    return pattern.matcher(this).matches()
}

fun LongRange.hasIntersect(other: LongRange): Boolean {
    val start = maxOf(this.first, other.first)
    val end = minOf(this.last, other.last)
    val union = if(start <= end) { start..end } else { null }
    return if (union == null) {
        false
    } else {
        !union.isEmpty()
    }
}

fun Long.between(start: Long, end: Long): Boolean {
    return this in start..end
}

fun Service.isForeground(): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (this.javaClass.name.equals(service.service.className)) {
            return service.foreground
        }
    }
    return false
}
