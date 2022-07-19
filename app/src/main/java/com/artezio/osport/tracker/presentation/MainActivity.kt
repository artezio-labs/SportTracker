package com.artezio.osport.tracker.presentation

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.data.permissions.PermissionsManager
import com.artezio.osport.tracker.data.permissions.SystemServicePermissionsManager
import com.artezio.osport.tracker.data.permissions.chain.Chain
import com.artezio.osport.tracker.data.permissions.chain.LocationPermissionLink
import com.artezio.osport.tracker.data.permissions.chain.NotificationPermissionLink
import com.artezio.osport.tracker.data.permissions.chain.PowerModePermissionLink
import com.artezio.osport.tracker.databinding.ActivityMainBinding
import com.artezio.osport.tracker.presentation.tracker.ScheduleTrackingBottomSheetDialog
import com.artezio.osport.tracker.util.DialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: ActivityViewModel by viewModels()

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val navController: NavController by lazy {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navHost.navController
    }

    private val permissionsManager: PermissionsManager = PermissionsManager(this)

    private val systemServicePermissionsManager = SystemServicePermissionsManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        changeStatusBarColor()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.sessionRecordingFragmentBottomNavItem -> {
                    val isNotificationsEnabled =
                        systemServicePermissionsManager.hasNotificationPermissionEnabled()
                    val isPowerSafeModeEnabled =
                        systemServicePermissionsManager.hasPowerSafeModePermissionEnabled()
                    Log.d("permissions_states", "$isNotificationsEnabled $isPowerSafeModeEnabled")
                    Log.d("permissions_states", "Manufacturer: ${Build.MANUFACTURER}")
                    if (permissionsManager.hasLocationPermissionsGranted()) {
                        if (systemServicePermissionsManager.hasNotificationPermissionEnabled()) {
                            if (!systemServicePermissionsManager.hasPowerSafeModePermissionEnabled()) {
                                Log.d("permissions_state", "All permissions is granted")
                                navController.navigate(R.id.action_mainFragment_to_sessionRecordingFragment)
                            } else {
                                Toast.makeText(
                                    this,
                                    getString(R.string.warning_turn_off_doze_mode),
                                    Toast.LENGTH_SHORT
                                ).show()
                                systemServicePermissionsManager.sendUserToPowerSettings()
                            }
                        } else {
                            systemServicePermissionsManager.sendUserToAppNotificationSettings()
                        }
                    } else {
                        permissionsManager.request()

                    }
                    Log.d("permissions_state", "onCreate: ")
                    false
                }
                else -> {
                    false
                }
            }

        }
    }

    // цепь для замены страшного вложенного ифа сверху
    // пока неравильно работает, доведу до ума попозже
    private fun buildChain() {
        var allPermissionsGranted: Boolean? = false
        val chain = Chain(this, navController)
        chain.process(LocationPermissionLink(this))
        chain.process(NotificationPermissionLink(this))
        chain.process(PowerModePermissionLink(this, navController))
    }

    override fun onBackPressed() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        viewModel.onBackPressed(navHost)
    }

    fun setBottomNavigationVisibility(visibility: Int) {
        binding.bottomNavigation.visibility = visibility
    }

    private fun changeStatusBarColor() {
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.app_status_bar_color)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}