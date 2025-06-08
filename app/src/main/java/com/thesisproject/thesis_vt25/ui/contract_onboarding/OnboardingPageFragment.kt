package com.thesisproject.thesis_vt25.ui.contract_onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class OnboardingPageFragment(): Fragment(){

    companion object {
        fun newInstance(layoutRes: Int) = OnboardingPageFragment().apply {
            arguments = Bundle().apply {
                putInt("layout", layoutRes)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout = arguments?.getInt("layout")?: 0
        return layoutInflater.inflate(layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}
