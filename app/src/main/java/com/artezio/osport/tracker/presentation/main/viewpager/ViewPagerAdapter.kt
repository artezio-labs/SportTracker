package com.artezio.osport.tracker.presentation.main.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.artezio.osport.tracker.presentation.main.FinishedTracksFragment
import com.artezio.osport.tracker.presentation.main.PlannedTracksFragment

class ViewPagerAdapter(fragment : Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            1 -> {
                FinishedTracksFragment()
            }
            else -> {
                PlannedTracksFragment()
            }
        }
    }

}