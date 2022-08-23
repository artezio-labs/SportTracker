package com.artezio.osport.tracker.di

import android.app.Application
import android.provider.Settings
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.artezio.osport.tracker.BuildConfig
import com.artezio.osport.tracker.data.logger.DeviceDetails
import com.artezio.osport.tracker.data.logger.TimberRemoteLogTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class SportTrackerApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            val deviceDetails = DeviceDetails(deviceId)
            val remoteTree = TimberRemoteLogTree(deviceDetails)

            Timber.plant(remoteTree)
        } else {
            //TODO plant timber release tree.
        }
    }
}