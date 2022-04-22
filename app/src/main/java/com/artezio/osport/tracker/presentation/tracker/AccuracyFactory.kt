package com.artezio.osport.tracker.presentation.tracker

class AccuracyFactory : IAccuracyFactory {
    override fun calculateAccuracy(accuracyValue: Float): IAccuracyFactory.AccuracyType =
        when {
            (0F..5.0F).contains(accuracyValue) -> {
                IAccuracyFactory.AccuracyType.GOOD
            }
            (5.01F..15F).contains(accuracyValue) -> {
                IAccuracyFactory.AccuracyType.MEDIUM
            }
            else -> {
                IAccuracyFactory.AccuracyType.BAD
            }
        }
}