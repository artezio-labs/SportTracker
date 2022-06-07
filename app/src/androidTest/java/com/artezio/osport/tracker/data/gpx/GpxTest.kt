package com.artezio.osport.tracker.data.gpx

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.artezio.osport.tracker.domain.model.LocationPointData
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GpxTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun test_write_data_to_file() {
        val gpx = GPX(context)
        val testData = listOf(
            LocationPointData(0.0, 0.0, 0.0, 0F, 0F, 0L, 0, 0L),
            LocationPointData(4.6, 7.4, 2.8, 0F, 0F, 40L, 0, 0L),
            LocationPointData(2.2, 6.3, 5.6, 0F, 0F, 0L, 6, 6L),
        )
        val file = gpx.write("Тестовый файл", testData)
        assertNotEquals(file?.length(),  0)
    }
}