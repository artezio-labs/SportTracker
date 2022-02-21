package com.artezio.osport.tracker.util

import java.util.*

fun millisecondsToDateFormat(milliseconds: Long): String =
    DATE_FORMATTER.format(Date(milliseconds))

fun dateToMilliseconds(date: String): Long =
    DATE_FORMATTER.parse(date).time