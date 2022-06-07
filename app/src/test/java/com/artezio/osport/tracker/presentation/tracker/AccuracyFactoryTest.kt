package com.artezio.osport.tracker.presentation.tracker

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccuracyFactoryTest {

    @Test
    fun `calculate accuracy test`() {
        val factory = AccuracyFactory()
        assertEquals(IAccuracyFactory.AccuracyType.BAD, factory.calculateAccuracy(19F))
        assertEquals(IAccuracyFactory.AccuracyType.MEDIUM, factory.calculateAccuracy(13F))
        assertEquals(IAccuracyFactory.AccuracyType.GOOD, factory.calculateAccuracy(4F))
    }
}