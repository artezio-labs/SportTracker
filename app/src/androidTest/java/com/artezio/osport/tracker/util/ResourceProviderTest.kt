package com.artezio.osport.tracker.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.artezio.osport.tracker.R
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResourceProviderTest {

    lateinit var resourceProvider: ResourceProvider

    @Before
    fun setUp() {
        resourceProvider =
            ResourceProvider(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext)
    }

    @Test
    fun test_getStringViaResourceProvider() {
        assertEquals(resourceProvider.getString(R.string.timer_just_started_text), "0 мин.")
        assertEquals(resourceProvider.getString(R.string.app_name), "Sport Tracker")
    }

    @Test(expected = Exception::class)
    fun test_getStringViaResourceProviderWithInvalidId() {
        resourceProvider.getString(Int.MIN_VALUE)
    }
}