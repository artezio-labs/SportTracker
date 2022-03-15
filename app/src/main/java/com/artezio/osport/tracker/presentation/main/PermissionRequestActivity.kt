package com.artezio.osport.tracker.presentation.main

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.artezio.osport.tracker.data.permissions.PermissionRequester
import com.artezio.osport.tracker.data.permissions.PermissionResult
import com.artezio.osport.tracker.data.permissions.PermissionState

class PermissionRequestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        if (savedInstanceState == null) {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val permissionResults = grantResults.zip(permissions).map { (grantResult, permission) ->
            val state =  when {
                grantResult == PackageManager.PERMISSION_GRANTED -> PermissionState.GRANTED
                ActivityCompat.shouldShowRequestPermissionRationale(this, permission) -> PermissionState.DENIED_TEMPORARILY
                else -> PermissionState.DENIED_PERMANENTLY
            }
            PermissionResult(permission, state)
        }

        finishWithResult(permissionResults)
    }

    private fun requestPermissions() {
        val permissions = intent?.getStringArrayExtra(PermissionRequester.PERMISSIONS_ARGUMENT_KEY) ?: arrayOf()
        val requestCode = intent?.getIntExtra(PermissionRequester.REQUEST_CODE_ARGUMENT_KEY, -1) ?: -1
        when {
            permissions.isNotEmpty() && requestCode != -1 -> ActivityCompat.requestPermissions(this, permissions, requestCode)
            else -> finishWithResult()
        }
    }

    private fun finishWithResult(permissionResults: List<PermissionResult> = listOf()) {
        val requestCode = intent?.getIntExtra(PermissionRequester.REQUEST_CODE_ARGUMENT_KEY, -1) ?: -1
        PermissionRequester.onPermissionResult(permissionResults, requestCode)
        finish()
    }
}