package com.artezio.osport.tracker.util

object ValidationUtils {

    fun validateNumber(input: String): Boolean {
        return input.toIntOrNull() is Int
    }

    fun isInRange(value: String, start: Int, end: Int): Boolean {
        return value.toIntOrNull() in start..end
    }
}