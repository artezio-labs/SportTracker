package com.artezio.osport.tracker.data.permissions.chain

import androidx.navigation.NavController
import com.artezio.osport.tracker.presentation.MainActivity

class Chain(
    private val activity: MainActivity,
    private val navController: NavController
) {
    private var chain: Link? = null
    private fun buildChain() {
        chain = LocationPermissionLink(activity)
            .linkWith(NotificationPermissionLink(activity))
            ?.linkWith(PowerModePermissionLink(activity, navController))
    }

    fun process(link: Link?): Boolean? {
        return link?.check()
    }

    init {
        buildChain()
    }
}