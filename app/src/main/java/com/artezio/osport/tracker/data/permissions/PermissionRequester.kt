package com.artezio.osport.tracker.data.permissions

import android.content.Context
import android.content.Intent
import com.artezio.osport.tracker.presentation.main.PermissionRequestActivity
import java.util.concurrent.ConcurrentHashMap

object PermissionRequester {
    const val PERMISSIONS_ARGUMENT_KEY = "PERMISSIONS_ARGUMENT_KEY"
    const val REQUEST_CODE_ARGUMENT_KEY = "REQUEST_CODE_ARGUMENT_KEY"

    private val callbackMap = ConcurrentHashMap<Int, (List<PermissionResult>) -> Unit>(1)
    private var requestCode = 256
        get() {
            requestCode = field--
            return if (field < 0) 255 else field
        }

    fun requestPermissions(
        context: Context,
        vararg permissions: String,
        callback: (List<PermissionResult>) -> Unit
    ): () -> Unit {
        val intent = Intent(context, PermissionRequestActivity::class.java).apply {
            putExtra(PERMISSIONS_ARGUMENT_KEY, permissions)
            putExtra(REQUEST_CODE_ARGUMENT_KEY, requestCode)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        callbackMap[requestCode] = callback
        return { callbackMap.remove(requestCode) }
    }

    internal fun onPermissionResult(responses: List<PermissionResult>, requestCode: Int) {
        callbackMap[requestCode]?.invoke(responses)
        callbackMap.remove(requestCode)
    }
}