package com.artezio.osport.tracker.presentation.main

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.artezio.osport.tracker.data.mappers.DomainToPresentationMapper
import com.artezio.osport.tracker.data.permissions.PermissionRequester
import com.artezio.osport.tracker.data.permissions.PermissionState
import com.artezio.osport.tracker.domain.model.EventWithData
import com.artezio.osport.tracker.domain.usecases.GetAllEventsWithDataUseCase
import com.artezio.osport.tracker.presentation.main.recycler.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllEventsWithDataUseCase: GetAllEventsWithDataUseCase,
    private val domainToPresentationMapper: DomainToPresentationMapper,
    private val context: Context
) : ViewModel() {


    val eventsWithDataFlow: Flow<List<EventWithData>>
        get() = getAllEventsWithDataUseCase.execute()

    fun buildListOfEvents(list: List<EventWithData>): List<Item> =
        list.map { domainToPresentationMapper.map(it) }
            .sortedByDescending { it.id }

    fun requestPermission(): () -> Unit {
        val permissions: Array<String> = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
        }
        return PermissionRequester.requestPermissions(
            context,
            *permissions
        ) { permissionResults ->
            for (permissionResult in permissionResults) {
                if (permissionResult.state == PermissionState.GRANTED) {
                    Toast.makeText(
                        context,
                        "Разрешение ${permissionResult.permission} предоставлено",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Разрешение ${permissionResult.permission} не предоставлено",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

}