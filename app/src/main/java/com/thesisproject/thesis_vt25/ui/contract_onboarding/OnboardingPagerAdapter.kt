package com.thesisproject.thesis_vt25.ui.contract_onboarding

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.thesisproject.thesis_vt25.R

class OnboardingPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingPageFragment.newInstance(R.layout.fragment_onboarding_layout_1)
            1 -> OnboardingPageFragment.newInstance(R.layout.fragment_onboarding_layout_2)
            2 -> OnboardingPageFragment.newInstance(R.layout.fragment_onboarding_layout_3)
            3 -> OnboardingPageFragment.newInstance(R.layout.fragment_onboarding_layout_4)
            4 -> OnboardingPageFragment.newInstance(R.layout.fragment_onboarding_layout_5)
            5 -> OnboardingPageFragment.newInstance(R.layout.fragment_onboarding_layout_6)
            else -> throw IllegalStateException("Invalid position $position")
        }
    }

    override fun getItemCount(): Int {
        return 6
    }
}