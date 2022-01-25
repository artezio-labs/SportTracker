package com.artezio.sporttracker.presentation.event

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.artezio.sporttracker.R
import com.artezio.sporttracker.databinding.FragmentEventCreateAndUpdateBinding

class EventCreateAndUpdateFragment : Fragment() {

    private var _binding: FragmentEventCreateAndUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventCreateAndUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}