package com.thesisproject.thesis_vt25.ui.my_contracts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.thesisproject.thesis_vt25.common.utils.promptOnboarding
import com.thesisproject.thesis_vt25.databinding.FragmentMyContractsBinding


class MyContractsFragment : Fragment() {

    private var _binding: FragmentMyContractsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewPagerAdapter: MyContractsViewPagerAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyContractsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        promptOnboarding(findNavController())

        val myContractsViewModel =
            ViewModelProvider(this)[MyContractsViewModel::class.java]
        viewPagerAdapter = MyContractsViewPagerAdapter(this)
        viewPager = binding.pagerMyContracts
        viewPager.adapter = viewPagerAdapter
        val tabLayout = binding.tabLayout

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Accepted Contracts"
                1 -> "Past Contracts"
                else -> null
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}