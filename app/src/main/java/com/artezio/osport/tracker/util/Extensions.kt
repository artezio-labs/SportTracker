package com.artezio.osport.tracker.util

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
