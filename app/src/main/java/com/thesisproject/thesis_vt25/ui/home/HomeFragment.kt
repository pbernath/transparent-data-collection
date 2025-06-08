package com.thesisproject.thesis_vt25.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.thesisproject.thesis_vt25.R
import com.thesisproject.thesis_vt25.databinding.FragmentHomeBinding
import com.thesisproject.thesis_vt25.ui.common.PrivacyTermsDialogFragment


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val root: View = binding.root

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.user.observe(viewLifecycleOwner) {
            it?.let { it1 ->
                if(!it1.onboarded){
                    binding.cardOnboarding.visibility = View.VISIBLE
                    binding.buttonHomeStartOnboarding.setOnClickListener {
                        findNavController().navigate(R.id.nav_onboarding)
                    }
                } else if(!it.privacyReminderDismissed) {
                    binding.cardOnboardingReminder.visibility = View.VISIBLE
                    binding.buttonHomeReadPolicy.setOnClickListener {
                        val dialog = PrivacyTermsDialogFragment.newInstance("privacy_policy.txt")
                        dialog.show(requireActivity().supportFragmentManager, "PolicyDialog")
                    }
                    binding.buttonHomeDismissOnboardingReminder.setOnClickListener {
                        binding.cardOnboarding.visibility = View.GONE
                        homeViewModel.setPrivacyReminderDismissed()
                    }
                }else{
                    binding.cardOnboardingReminder.visibility = View.GONE
                    binding.cardOnboarding.visibility = View.GONE

                }
            }
        }

    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}