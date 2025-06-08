package com.thesisproject.thesis_vt25.ui.contract_onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.thesisproject.thesis_vt25.databinding.FragmentOnboardingBinding

class OnboardingFragment(): Fragment(){
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPagerAdapter: OnboardingPagerAdapter
    private lateinit var viewPager: ViewPager2


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)

        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var onLastPage = false
        viewPagerAdapter = OnboardingPagerAdapter(this)
        viewPager = binding.pagerMyContracts
        viewPager.adapter = viewPagerAdapter
        val onboardingViewModel =
            ViewModelProvider(this)[OnboardingViewModel::class.java]

        onboardingViewModel.currentIndex.observe(viewLifecycleOwner) {
            viewPager.currentItem = it
            if (it == viewPager.adapter?.itemCount?.minus(1)){
                onLastPage = true
                binding.buttonOnboardingNext.text = "Finish tutorial"
                if (!onboardingViewModel.previouslyCompleted){
                    binding.checkOnboardingUnderstood.visibility = View.VISIBLE
                }
            } else{
                onLastPage = false
                binding.buttonOnboardingNext.text = "Next"
                binding.checkOnboardingUnderstood.visibility = View.GONE

            }
            if (it == 0) {
                binding.buttonOnboardingPrevious.visibility = View.INVISIBLE
            }else{
                binding.buttonOnboardingPrevious.visibility = View.VISIBLE
            }
        }

        binding.buttonOnboardingPrevious.setOnClickListener {
            onboardingViewModel.setIndex(viewPager.currentItem - 1, viewPager.adapter?.itemCount)
        }
        binding.buttonOnboardingNext.setOnClickListener {
            if (onLastPage == true){
                if(onboardingViewModel.previouslyCompleted || binding.checkOnboardingUnderstood.isChecked){
                    onboardingViewModel.setIndex(viewPager.currentItem + 1, viewPager.adapter?.itemCount)
                }else{
                    Toast.makeText(this.context,
                        "You need to confirm that you have understood the tutorial.",
                        Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            } else{
                onboardingViewModel.setIndex(viewPager.currentItem + 1, viewPager.adapter?.itemCount)
            }
        }
        onboardingViewModel.complete.observe(viewLifecycleOwner){
            if (it){
                findNavController().popBackStack()
                Toast.makeText(this.context, "You can now access the Contract features!", Toast.LENGTH_LONG).show()
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
