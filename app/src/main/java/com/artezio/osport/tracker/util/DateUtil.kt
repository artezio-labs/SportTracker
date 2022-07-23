package com.artezio.osport.tracker.util

import com.artezio.osport.tracker.R
import java.util.*
import kotlin.math.roundToInt

fun millisecondsToDateFormat(milliseconds: Long): String =
    DATE_FORMATTER.format(Date(milliseconds))

fun millisecondsToDateFormatForPlanning(milliseconds: Long): String =
    DATE_FORMATTER_FOR_PLANNING.format(Date(milliseconds))

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

fun formatTime(time: Double, resourceProvider: ResourceProvider): String {
    if (time <= TIMER_START_POINT) {
        return resourceProvider.getString(R.string.timer_just_started_text)
    }
    val sb = StringBuilder()
    val hours = (time / 3600).toInt()
    val minutes = ((time % 3600) / 60).toInt()
    val seconds = (time % 60).toInt()
    if (hours >= 1)
        sb.append("$hours ч. ")
    if (minutes >= 1)
        sb.append("$minutes мин. ")
    if (seconds != 0)
        sb.append("$seconds сек.")
    return sb.toString()
}

fun formatEventName(milliseconds: Long): String =
    EVENT_NAME_FORMATTER.format(Date(milliseconds))

fun convertHoursOrMinutesToMilliseconds(time: Int, timeUnit: Int): Long {
    return when (timeUnit) {
        Calendar.HOUR -> time * HOUR_IN_MILLIS
        Calendar.MINUTE -> time * MINUTE_IN_MILLIS
        else -> throw IllegalArgumentException("Unknown time unit")
    }
}

fun convertMillisTo(millis: Long, calendarTimeUnit: Int): Int {
    return when (calendarTimeUnit) {
        Calendar.HOUR -> (millis / HOUR_IN_MILLIS).toInt()
        Calendar.MINUTE -> (millis / MINUTE_IN_MILLIS).toInt()
        else -> 0
    }
}