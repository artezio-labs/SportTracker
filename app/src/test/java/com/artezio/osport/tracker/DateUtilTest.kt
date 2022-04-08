package com.artezio.osport.tracker

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.util.dateToMilliseconds
import com.artezio.osport.tracker.util.getTimerStringFromDouble
import com.artezio.osport.tracker.util.millisecondsToDateFormat
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DateUtilTest {

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

}