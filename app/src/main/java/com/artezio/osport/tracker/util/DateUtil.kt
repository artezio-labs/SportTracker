package com.artezio.osport.tracker.util

import java.util.*
import kotlin.math.roundToInt

fun millisecondsToDateFormat(milliseconds: Long): String =
    DATE_FORMATTER.format(Date(milliseconds))

fun dateToMilliseconds(date: String): Long =
    DATE_FORMATTER.parse(date).time

fun getTimerStringFromDouble(time: Double): String {
    if (time < 0) return "00:00:00"
    val timeInt = time.roundToInt()
    val hours = timeInt % 86400 / 3600
    val minutes = timeInt % 86400 % 3600 / 60
    val seconds = timeInt % 86400 % 3600 % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}