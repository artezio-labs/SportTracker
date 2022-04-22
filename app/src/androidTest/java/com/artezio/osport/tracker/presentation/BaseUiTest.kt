package com.artezio.osport.tracker.presentation

import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavDeepLinkBuilder
import androidx.test.platform.app.InstrumentationRegistry
import com.artezio.osport.tracker.R
import junit.framework.TestCase

open class BaseUiTest : TestCase() {

    fun buildFragmentLauncherIntent(destinationId: Int, args: Bundle?): Intent =
        NavDeepLinkBuilder(InstrumentationRegistry.getInstrumentation().targetContext)
            .setGraph(R.navigation.bottom_nav)
            .setComponentName(MainActivity::class.java)
            .setDestination(destinationId)
            .setArguments(args)
            .createTaskStackBuilder().intents[0]
}