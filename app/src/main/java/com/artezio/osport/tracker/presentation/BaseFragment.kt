package com.artezio.osport.tracker.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<B : ViewBinding> : Fragment() {

    private var _binding: B? = null
    protected val binding get() = checkNotNull(_binding)

    protected open var bottomNavigationViewVisibility = View.VISIBLE

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

    override fun onResume() {
        super.onResume()
        setVisibility()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    private fun setVisibility() {
        if (activity is MainActivity) {
            val mainActivity = activity as MainActivity
            mainActivity.setBottomNavigationVisibility(bottomNavigationViewVisibility)
        }
    }
}