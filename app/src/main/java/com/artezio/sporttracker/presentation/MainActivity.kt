package com.artezio.sporttracker.presentation

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.artezio.sporttracker.R
import com.artezio.sporttracker.data.trackservice.TrackService
import com.artezio.sporttracker.databinding.ActivityMainBinding
import com.artezio.sporttracker.util.START_FOREGROUND_SERVICE
import com.artezio.sporttracker.util.STOP_FOREGROUND_SERVICE
import com.artezio.sporttracker.util.hasLocationPermission
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var data: Int? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            data = intent?.getIntExtra("steps", -1)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        requestLocationPermissions()
        requestPedometerPermissions()

        binding.textViewSteps.setText("$data")

        binding.startStepCounter.setOnClickListener {
            val intent = Intent(this, TrackService::class.java).apply {
                action = START_FOREGROUND_SERVICE
            }
            startService(intent)
        }

        binding.stopStepCounter.setOnClickListener {
            val intent = Intent(this, TrackService::class.java).apply {
                STOP_FOREGROUND_SERVICE
            }
            stopService(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter("STEPS_FILTER")
        registerReceiver(receiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    private fun requestPedometerPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept activity recognition permissions to use this app.",
                4836,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
        }
    }

    private fun requestLocationPermissions() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                9465,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                9465,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }
}