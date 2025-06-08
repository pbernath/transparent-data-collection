package com.thesisproject.thesis_vt25.ui.my_profile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.thesisproject.thesis_vt25.ui.common.PrivacyTermsPageFragment

class MyProfileViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyProfilePageFragment()
            1 -> PrivacyTermsPageFragment.newInstance("privacy_policy.txt")
            2 -> PrivacyTermsPageFragment.newInstance("terms_of_service.txt")
            else -> throw IllegalStateException("Invalid position $position")
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}