package com.artezio.osport.tracker.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.artezio.osport.tracker.presentation.navigation.NavigationCommand
import com.artezio.osport.tracker.util.observeNonNull

abstract class BaseFragment<B : ViewBinding, VM : BaseViewModel> : Fragment(), IOnBackPressed {

    private var _binding: B? = null
    protected val binding get() = checkNotNull(_binding)

    protected open var bottomNavigationViewVisibility = View.VISIBLE

    protected abstract val viewModel: VM

    override var onBackPressed: Boolean = true

    override fun onStart() {
        super.onStart()
        setVisibility()
    }
    abstract fun initBinding(inflater: LayoutInflater, container: ViewGroup?): B

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = initBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeNavigation()
    }

    override fun onResume() {
        super.onResume()
        setVisibility()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun observeNavigation() {
        viewModel.navigation.observeNonNull(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { navigationCommand ->
                handleNavigation(navigationCommand)
            }
        }
    }

    private fun handleNavigation(navigationCommand: NavigationCommand) {
        when (navigationCommand) {
            is NavigationCommand.ToDirection -> {
                findNavController().navigate(navigationCommand.directions)
            }
            is NavigationCommand.Back -> {
                if (navigationCommand.directions == null) {
                    findNavController().navigateUp()
                } else {
                    findNavController().navigate(navigationCommand.directions)
                }
            }
        }
    }

    private fun setVisibility() {
        if (activity is MainActivity) {
            val mainActivity = activity as MainActivity
            mainActivity.setBottomNavigationVisibility(bottomNavigationViewVisibility)
        }
    }
}