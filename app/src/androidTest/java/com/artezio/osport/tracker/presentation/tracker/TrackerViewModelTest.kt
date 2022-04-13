package com.artezio.osport.tracker.presentation.tracker

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artezio.osport.tracker.databinding.FragmentSessionRecordingBinding
import com.artezio.osport.tracker.databinding.FragmentTrackerStatisticsBinding
import com.artezio.osport.tracker.domain.usecases.GetLastEventIdUseCase
import com.artezio.osport.tracker.domain.usecases.GetLocationsByEventIdUseCase
import com.artezio.osport.tracker.domain.usecases.InsertEventUseCase
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrackerViewModelTest {

    private lateinit var getLastEventIdUseCase: GetLastEventIdUseCase
    private lateinit var insertEventUseCase: InsertEventUseCase
    private lateinit var getLocationsByEventIdUseCase: GetLocationsByEventIdUseCase
    private lateinit var viewModel: TrackerViewModel
    private lateinit var context: Context
    private lateinit var googleMap: GoogleMap
    private lateinit var location: LatLng
    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var bindingSessionFragment: FragmentSessionRecordingBinding
    private lateinit var bindingTrackerStatistics: FragmentTrackerStatisticsBinding

    private val testMainScope = CoroutineScope(Dispatchers.Main)

    @Before
    fun setUp() {
        getLastEventIdUseCase = mockk(relaxed = true)
        insertEventUseCase = mockk(relaxed = true)
        getLocationsByEventIdUseCase = mockk(relaxed = true)
        viewModel = TrackerViewModel(
            getLastEventIdUseCase,
            insertEventUseCase,
            getLocationsByEventIdUseCase
        )
        context = mockk(relaxed = true)
        googleMap = mockk(relaxed = true)
        location = mockk(relaxed = true)
        lifecycleOwner = mockk(relaxed = true)
        bindingSessionFragment = mockk(relaxed = true)
        bindingTrackerStatistics = mockk(relaxed = true)
    }

    @Test
    fun getLastEventIdFlow() {
        viewModel.lastEventIdFlow
        verify { getLastEventIdUseCase.execute() }
    }

    @Test
    fun getLocationsByEventId() {
        viewModel.getLocationsByEventId(1)
        coVerify { getLocationsByEventIdUseCase.execute(1) }
    }

    @Test
    fun generateEvent() {
        viewModel.generateEvent()
        coVerify { insertEventUseCase.execute(any()) }
    }

    @Test
    fun startService() {
        viewModel.startService(context, 1)
        verify { context.startService(any()) }
    }

    @Test
    fun stopService() {
        testMainScope.launch {
            viewModel.stopService(context)
            verify { context.stopService(any()) }
        }
    }

    @Test
    fun pauseTracking() {
        viewModel.pauseTracking(context)
        verify { context.startService(any()) }
    }

    @Test
    fun resumeTracking() {
        viewModel.resumeTracking(context)
        verify { context.startService(any()) }
    }
}