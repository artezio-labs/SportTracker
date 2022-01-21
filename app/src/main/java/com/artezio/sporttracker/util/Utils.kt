package com.artezio.sporttracker.util

import java.text.SimpleDateFormat
import java.util.*

fun millisecondsToDateFormat(milliseconds: Long): String =
    SimpleDateFormat("yyyy-MM-dd'HH:mm:ss", Locale.getDefault()).format(Date(milliseconds))