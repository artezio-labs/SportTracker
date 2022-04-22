package com.artezio.osport.tracker.presentation.tracker

import com.artezio.osport.tracker.R

interface IAccuracyFactory {
    enum class AccuracyType(val color: Int) {
        GOOD(R.color.accuracy_good_color),
        MEDIUM(R.color.accuracy_medium_color),
        BAD(R.color.accuracy_bad_color)
    }
    fun calculateAccuracy(accuracyValue: Float): AccuracyType
}