package com.artezio.osport.tracker.data.permissions

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.artezio.osport.tracker.util.DialogBuilder

class SystemServicePermissionsManager(
    private val context: Context
) : ISystemServicePermissionsManager {

    override fun hasNotificationPermissionEnabled(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    override fun hasPowerSafeModePermissionEnabled(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        var value = 0
        return when (Build.MANUFACTURER.trim()) {
            "Xiaomi" -> {
                try {
                    value = Settings.System.getInt(context.contentResolver, "POWER_SAVE_MODE_OPEN")
                    value == 1
                } catch (e: Settings.SettingNotFoundException) {
                    Log.e("permissions_states", e.message.toString())
                    false
                }
            }
            "Huawei" -> {
                try {
                    value = Settings.System.getInt(context.contentResolver, "SmartModeStatus")
                    value == 4
                } catch (e: Settings.SettingNotFoundException) {
                    Log.e("permissions_states", e.message.toString())
                    false
                }
            }
            "HTC" -> {
                try {
                    value = Settings.System.getInt(context.contentResolver, "user_powersaver_enable")
                    value == 1
                } catch (e: Settings.SettingNotFoundException) {
                    Log.e("permissions_states", e.message.toString())
                    false
                }
            }
            "Samsung" -> {
                try {
                    value = Settings.System.getInt(context.contentResolver, "psm_switch")
                    value == 1
                } catch (e: Settings.SettingNotFoundException) {
                    Log.e("permissions_states", e.message.toString())
                    false
                }
            }
            else -> { powerManager.isPowerSaveMode }
        }
    }

    override fun hasGPSEnabled(): LiveData<Boolean> {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return liveData { emit(isGpsEnabled) }
    }

    override fun sendUserToPowerSettings() {
        DialogBuilder(
            context = context,
            title = "Внимание",
            message = """
                Приложению требуется, чтобы режим экономии энергии был выключен, чтобы получаемые данные о тренировке были точными.
                
                С включенным режимом энергосбережения данные о тренировке могут быть не точными.
                
                Перейти в настройки, чтобы выключить режим экономии энергии?
            """.trimIndent(),
            positiveButtonText = "Да",
            positiveButtonClick = { _, _ ->
                context.startActivity(Intent(Settings.ACTION_SETTINGS))
            },
            negativeButtonText = "Не сейчас",
            negativeButtonClick = { dialog, _ -> dialog.cancel() }
        ).build()
    }

    override fun sendUserToAppNotificationSettings() {
        DialogBuilder(
            context = context,
            title = "Внимание",
            message = """
                Приложению требуется разрешение на показ уведомлений, чтобы данные могли записываться в фоне.
                
                С выключенными уведомлениями данные могут перестать записываться в фоновом режиме.
                
                Перейти в настройки, чтобы включить уведомления?
            """.trimIndent(),
            positiveButtonText = "Да",
            positiveButtonClick = { _, _ ->
                val appSettingsIntent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(appSettingsIntent)
            },
            negativeButtonText = "Не сейчас",
            negativeButtonClick = { dialog, _ ->
                dialog.cancel()
            }
        ).build()
    }


}