package com.thesisproject.thesis_vt25.ui.my_contracts

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyContractsViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AcceptedContractsPageFragment()
            1 -> PastContractsPageFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}