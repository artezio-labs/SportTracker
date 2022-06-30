package com.artezio.osport.tracker.data.permissions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.presentation.MainActivity
import com.artezio.osport.tracker.util.DialogBuilder

class PermissionsManager(
    private val activity: MainActivity
) : IPermissionsManager {



    private val permissionsToBeRequested =
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION,
        ) else arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )

    private val permissionsForAndroidROrAbove = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACTIVITY_RECOGNITION,
        NOTIFICATION_ANDROID13_PERMISSION
    )

    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    private val accessBackgroundLocationPermission =
        Manifest.permission.ACCESS_BACKGROUND_LOCATION

    fun request() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            requestPermissionsAndroidQAndBelow()
        } else {
            requestPermissionsAndroidRAndAbove()
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(accessBackgroundLocationPermission),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }

    private fun requestPermissionsAndroidRAndAbove() {
        ActivityCompat.requestPermissions(
            activity,
            permissionsForAndroidROrAbove,
            PERMISSIONS_REQUEST_CODE
        )
    }

    private fun requestPermissionsAndroidQAndBelow() {
        ActivityCompat.requestPermissions(
            activity,
            permissionsToBeRequested,
            PERMISSIONS_REQUEST_CODE
        )
    }

    override fun hasLocationPermissionsGranted(): Boolean {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        } else {
            locationPermissions
        }
        for (permission in permissions) {
            if (!isPermissionGranted(permission)) {
                return false
            }
        }
        return true
    }

    override fun requestMultiplePermissions() {
        val remainingPermissions: MutableList<String> = mutableListOf()
        val permissionsToRequest = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            permissionsToBeRequested
        } else {
            permissionsForAndroidROrAbove
        }
        Log.d(
            "permissions_states",
            "Permissions, which needs to be granted: ${permissionsToRequest.contentToString()}"
        )
        permissionsToRequest.forEach { permission ->
            if (!isPermissionGranted(permission)) {
                remainingPermissions.add(permission)
            }
            if (isPermissionGranted(locationPermissions[0])
                && isPermissionGranted(locationPermissions[1])
                && !remainingPermissions.contains(accessBackgroundLocationPermission)
            ) {
                remainingPermissions.add(accessBackgroundLocationPermission)
            }
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                activity,
                remainingPermissions.toTypedArray(),
                PERMISSIONS_REQUEST_CODE
            )
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q
            && !hasLocationPermissionsGranted()
        ) {
            requestAccessBackgroundLocationPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        if (requestCode != PERMISSIONS_REQUEST_CODE) {
            return true
        }
        grantResults.forEachIndexed { i, _ ->
            Log.d("permissions_states", "Permission: ${permissions[i]} granted: ${grantResults[i]}")
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED
                && permissions[i] != Manifest.permission.ACTIVITY_RECOGNITION
            ) {
                Log.d(
                    "permissions_state",
                    "${shouldShowRequestPermissionRationale(activity, permissions[i])}"
                )
                if (shouldShowRequestPermissionRationale(activity, permissions[i])) {
                    showOnPermissionDeniedDialog()
                } else {
                    askUserForOpeningAppSettings()
                }
                return false
            }
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (!hasLocationPermissionsGranted()) {
                showAccessBackgroundLocationPermissionDialog()
            }
        }
        return true
    }

    private fun shouldShowRequestPermissionRationale(activity: Activity, permission: String) =
        ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)

    private fun showOnPermissionDeniedDialog() {
        DialogBuilder(
            activity,
            "Внимание",
            """
                Приложению требуется постоянный доступ к местоположению пользователя, чтобы оно могло записывать данные тренировок.
                
                Без разрешения использования "в любом режиме", вы не сможете записывать свои тренировки.
                
                Выдать разрешение?
            """.trimIndent(),
            "Да",
            { dialog, _ ->
                dialog.cancel()
                request()
            },
            "Не сейчас",
            { dialog, _ ->
                dialog.cancel()
            }
        ).build()
    }

    private fun askUserForOpeningAppSettings() {
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity.packageName, null)
        )
        if (activity.packageManager.resolveActivity(
                appSettingsIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            ) == null
        ) {
            Toast.makeText(
                activity,
                activity.getString(R.string.permissions_settings_toast_text),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            DialogBuilder(
                activity,
                "Внимание",
                """
                Приложению требуется постоянный доступ к местоположению пользователя, чтобы оно могло записывать данные тренировок.
                
                Без доступа вы не сможете записывать свои тренировки.
                
                Открыть настройки, чтобы выдать разрешение?
            """.trimIndent(),
                "Да",
                { _, _ ->
                    activity.startActivity(appSettingsIntent)
                },
                "Не сейчас",
                { dialog, _ ->
                    dialog.dismiss()
                }
            ).build()
        }
    }

    private fun showAccessBackgroundLocationPermissionDialog() {
        DialogBuilder(
            activity,
            "Внимание",
            """
                Приложению требуется разрешение на отслеживание местоположения в фоновом режиме, без этого разрешения приложение не сможет записывать данные о тренировках.
                
                Без этого разрешения вы не сможете записывать тренировки.
                
                Открыть настройки, чтобы выдать разрешение?
            """.trimIndent(),
            "Да",
            { _, _ ->
                requestAccessBackgroundLocationPermission()
            },
            "Не сейчас",
            { dialog, _ ->
                dialog.dismiss()
            }
        ).build()
    }

    private fun requestAccessBackgroundLocationPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(accessBackgroundLocationPermission),
            PERMISSIONS_REQUEST_CODE
        )
    }

    fun isPermissionGranted(permission: String): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(activity, permission)
        Log.d(
            "permissions_states",
            "Permission $permission before request state: $permissionState"
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 1234
        private const val NOTIFICATION_ANDROID13_PERMISSION = "android.permission.POST_NOTIFICATIONS"
    }
}