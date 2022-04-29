package com.artezio.osport.tracker.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DateUtilTest : TestCase() {

    private lateinit var resourceProvider: ResourceProvider

    @Before
    fun setup() {
        resourceProvider = mockk {
            every { getString(any()) } returns "0 мин."
        }
    }

    @Test
    fun `milliseconds to date`() = runBlocking {
        assertEquals("01-01-1970 03:00:00", millisecondsToDateFormat(0))
        assertEquals("04-04-2022 01:43:28", millisecondsToDateFormat(1649025808501))
        assertEquals("30-09-1917 04:47:50", millisecondsToDateFormat(-1649025808501))
    }

    @Test
    fun `date to milliseconds`() = runBlocking {
        assertEquals(dateToMilliseconds("01-01-1970 00:00:00"), -10800000)
        assertEquals(dateToMilliseconds("04-04-2022 01:43:28"), 1649025808000L)
        assertEquals(dateToMilliseconds("30-09-1917 04:47:50"), -1649025809000L)
    }

    @Test
    fun `convert seconds to timer string format hh-mm-ss`() = runBlocking {
        assertEquals(getTimerStringFromDouble(0.0), "00:00:00")
        assertEquals(getTimerStringFromDouble(-12.0), "00:00:00")
        assertEquals(getTimerStringFromDouble(60.0), "00:01:00")
        assertEquals(getTimerStringFromDouble(3600.0), "01:00:00")
    }

    @Test
    fun `convert double seconds into string format`() {
        assertEquals(formatTime(0.0, resourceProvider), "0 мин.")
        assertEquals(formatTime(5.0, resourceProvider), "5 сек.")
        assertEquals(formatTime(60.0, resourceProvider), "1 мин. ")
        assertEquals(formatTime(3600.0, resourceProvider), "1 ч. ")
        assertEquals(formatTime(3666.0, resourceProvider), "1 ч. 1 мин. 6 сек.")
        assertEquals(formatTime(-1.0, resourceProvider), "0 мин.")
    }

}