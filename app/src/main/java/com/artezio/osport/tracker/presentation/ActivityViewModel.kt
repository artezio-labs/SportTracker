package com.artezio.osport.tracker.presentation

import androidx.navigation.fragment.NavHostFragment
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.presentation.event.SaveEventFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(

) : BaseViewModel() {

    fun onBackPressed(navHost: NavHostFragment) {
        val fragment = navHost.childFragmentManager.fragments.first()
        val backPressed = fragment as? IOnBackPressed
        backPressed?.onBackPressed?.takeIf { !it }?.let {
            if (fragment is SaveEventFragment) {
                navHost.navController.navigate(R.id.backToMainDialog)
            } else {
                navHost.navController.navigateUp()
            }
        }
    }
}