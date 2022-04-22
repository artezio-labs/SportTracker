package com.artezio.osport.tracker.util

import java.text.SimpleDateFormat
import java.util.*

const val START_FOREGROUND_SERVICE = "START_FOREGROUND"
const val STOP_FOREGROUND_SERVICE = "STOP_FOREGROUND"
const val PAUSE_FOREGROUND_SERVICE = "PAUSE_FOREGROUND"
const val RESUME_FOREGROUND_SERVICE = "RESUME_FOREGROUND"

val DATE_FORMATTER = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())

const val TIMER_START_POINT = 0.0