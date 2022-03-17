package com.artezio.osport.tracker.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.FragmentParentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParentMainFragment : BaseFragment<FragmentParentMainBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("navigation", "onViewCreated: ")
        val navHost =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHost.navController
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentParentMainBinding =
        FragmentParentMainBinding.inflate(inflater, container, false)

}