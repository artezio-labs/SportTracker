package com.artezio.osport.tracker.data.permissions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.artezio.osport.tracker.presentation.MainActivity
import com.artezio.osport.tracker.util.DialogBuilder

class SystemServicePermissionsManager(
    private val activity: MainActivity
) : ISystemServicePermissionsManager {

    override fun hasNotificationPermissionEnabled(): Boolean {
        return NotificationManagerCompat.from(activity).areNotificationsEnabled()
    }

    override fun hasPowerSafeModePermissionEnabled(): Boolean {
        val powerManager = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
        var value = 0
        return when (Build.MANUFACTURER.trim()) {
            "Xiaomi" -> {
                try {
                    value = Settings.System.getInt(activity.contentResolver, "POWER_SAVE_MODE_OPEN")
                    value == 1
                } catch (e: Settings.SettingNotFoundException) {
                    Log.e("permissions_states", e.message.toString())
                    false
                }
            }
            "Huawei" -> {
                try {
                    value = Settings.System.getInt(activity.contentResolver, "SmartModeStatus")
                    value == 4
                } catch (e: Settings.SettingNotFoundException) {
                    Log.e("permissions_states", e.message.toString())
                    false
                }
            }
            "HTC" -> {
                try {
                    value = Settings.System.getInt(activity.contentResolver, "user_powersaver_enable")
                    value == 1
                } catch (e: Settings.SettingNotFoundException) {
                    Log.e("permissions_states", e.message.toString())
                    false
                }
            }
            "Samsung" -> {
                try {
                    value = Settings.System.getInt(activity.contentResolver, "psm_switch")
                    value == 1
                } catch (e: Settings.SettingNotFoundException) {
                    Log.e("permissions_states", e.message.toString())
                    false
                }
            }
            else -> { powerManager.isPowerSaveMode }
        }
    }

    override fun sendUserToPowerSettings() {
        DialogBuilder(
            context = activity,
            title = "Внимание",
            message = """
                Приложению требуется, чтобы режим экономии энергии был выключен, чтобы получаемые данные о тренировке были точными.
                
                С включенным режимом энергосбережения данные о тренировке могут быть не точными.
                
                Перейти в настройки, чтобы выключить режим экономии энергии?
            """.trimIndent(),
            positiveButtonText = "Да",
            positiveButtonClick = { _, _ ->
                activity.startActivity(Intent(Settings.ACTION_SETTINGS))
            },
            negativeButtonText = "Не сейчас",
            negativeButtonClick = { dialog, _ -> dialog.cancel() }
        ).build()
    }

    override fun sendUserToAppNotificationSettings() {
        DialogBuilder(
            context = activity,
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
                    Uri.fromParts("package", activity.packageName, null)
                )
                activity.startActivity(appSettingsIntent)
            },
            negativeButtonText = "Не сейчас",
            negativeButtonClick = { dialog, _ ->
                dialog.cancel()
            }
        ).build()
    }


}