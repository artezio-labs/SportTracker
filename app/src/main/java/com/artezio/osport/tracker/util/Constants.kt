package com.artezio.osport.tracker.util

import java.text.SimpleDateFormat
import java.util.*

const val START_FOREGROUND_SERVICE = "START_FOREGROUND"
const val STOP_FOREGROUND_SERVICE = "STOP_FOREGROUND"
const val PAUSE_FOREGROUND_SERVICE = "PAUSE_FOREGROUND"
const val RESUME_FOREGROUND_SERVICE = "RESUME_FOREGROUND"
const val START_PLANNED_SERVICE = "START_PLANNED"

val DATE_FORMATTER = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
val EVENT_NAME_FORMATTER = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

const val TIMER_START_POINT = 0.0
const val CADENCE_STEP_FILTER_VALUE = 3_000
const val MINUTE = 60_000