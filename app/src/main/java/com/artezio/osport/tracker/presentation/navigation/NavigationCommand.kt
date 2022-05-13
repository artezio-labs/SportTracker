package com.artezio.osport.tracker.presentation.navigation

import androidx.navigation.NavDirections

sealed class NavigationCommand {
    data class ToDirection(val directions: NavDirections) : NavigationCommand()
    data class Back(val directions: NavDirections? = null) : NavigationCommand()
}