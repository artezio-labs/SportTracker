package com.artezio.osport.tracker.util

import android.util.Log
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
        Calendar.HOUR_OF_DAY -> time * HOUR_IN_MILLIS
        Calendar.MINUTE -> time * MINUTE_IN_MILLIS
        else -> throw IllegalArgumentException("Unknown time unit")
    }
}

fun convertMillisTo(millis: Long, calendarTimeUnit: Int): Int {
    val calendar = Calendar.getInstance()
    return when (calendarTimeUnit) {
        Calendar.HOUR_OF_DAY -> {
            calendar.timeInMillis = millis
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            Log.d("pick_time", "hour: $hour")
            hour
        }
        Calendar.MINUTE -> {
            calendar.timeInMillis = millis
            val minute = calendar.get(Calendar.MINUTE)
            Log.d("pick_time", "minute: $minute")
            minute
        }
        else -> 0
    }
}

fun compareTimeWithNow(hour: Int, minute: Int): Boolean {
    Log.d("pick_time", "Hour: $hour, Minute: $minute")
    val calendar = Calendar.getInstance()
    Log.d(
        "pick_time",
        "Calendar hour: ${calendar.get(Calendar.HOUR)}, minute: ${calendar.get(Calendar.MINUTE)}"
    )
    return calendar.get(Calendar.HOUR_OF_DAY) > hour ||
            (calendar.get(Calendar.HOUR_OF_DAY) == hour && calendar.get(Calendar.MINUTE) >= minute)
}

fun getTimeFromMillis(millis: Long, calendarTimeUnit: Int): Int {
    return when (calendarTimeUnit) {
        Calendar.HOUR_OF_DAY -> (millis / HOUR_IN_MILLIS).toInt()
        Calendar.MINUTE -> (millis / MINUTE_IN_MILLIS).toInt()
        else -> 0
    }
}

fun compareTimeWithNow(time: Long): Boolean {
    return time < System.currentTimeMillis() % 1_000_000L
}

fun getTimeFromMillis(millis: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = millis
    return String.format(
        "%02d:%02d",
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE)
    )
}

fun isTimeLowerThanNow(time: Long): Boolean {
    val calendarTime = Calendar.getInstance()
    val calendarNow = Calendar.getInstance()
    calendarTime.timeInMillis = time
    return calendarTime.get(Calendar.HOUR_OF_DAY) < calendarNow.get(Calendar.HOUR_OF_DAY) ||
            (calendarTime.get(Calendar.HOUR_OF_DAY) == calendarNow.get(Calendar.HOUR_OF_DAY) &&
                    calendarTime.get(Calendar.MINUTE) < calendarNow.get(Calendar.MINUTE))
}

fun getCurrentTimeMillis(plus: Long = 0L): Long {
    return Calendar.getInstance().timeInMillis + plus
}

fun getMillisFromTimeString(time: String): Long {
    if (time.trim().isBlankOrEmpty()) return 0L
    val calendar = Calendar.getInstance()
    val timeArray = time.split(":")
    calendar.set(Calendar.HOUR_OF_DAY, timeArray[0].toInt())
    calendar.set(Calendar.MINUTE, timeArray[1].toInt())
    return calendar.timeInMillis
}

// fun validate hour and minutes greater than now
fun validateTime(time: String): Boolean {
    val calendarTime = Calendar.getInstance()
    val calendarNow = Calendar.getInstance()
    val timeArray = time.split(":")
    val timeArrayNow = getTimeFromMillis(System.currentTimeMillis()).split(":")
    Log.d("pick_time", "Time picked: ${timeArray.joinToString(":")} \n Time now: ${timeArrayNow.joinToString(":")}")
    calendarTime.set(Calendar.HOUR_OF_DAY, timeArray[0].toInt())
    calendarTime.set(Calendar.MINUTE, timeArray[1].toInt())
    calendarNow.set(Calendar.HOUR_OF_DAY, timeArrayNow[0].toInt())
    calendarNow.set(Calendar.MINUTE, timeArrayNow[1].toInt())
    return calendarTime.timeInMillis > calendarNow.timeInMillis
}

fun getMillisFromDateString(date: String): Long {
    val calendar = Calendar.getInstance()
    val dateArray = date.split("-")
    calendar.set(Calendar.YEAR, dateArray[2].toInt())
    calendar.set(Calendar.MONTH, dateArray[1].toInt() - 1)
    calendar.set(Calendar.DAY_OF_MONTH, dateArray[0].toInt())
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    return calendar.timeInMillis
}

fun getMillisFromString(time: String): Long {
    val hoursAndMinutes = time.split(":").map { it.toInt() }
    return hoursAndMinutes[0] * HOUR_IN_MILLIS + hoursAndMinutes[1] * MINUTE_IN_MILLIS
}




