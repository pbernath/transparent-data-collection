package com.thesisproject.thesis_vt25.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.thesisproject.thesis_vt25.databinding.FragmentPrivacyTermsPageBinding

class PrivacyTermsPageFragment() : Fragment() {
    companion object {
        private const val ARG_TYPE = "policy_type"

        fun newInstance(type: String): PrivacyTermsPageFragment {
            val fragment = PrivacyTermsPageFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_TYPE, type)
            }
            return fragment
        }
    }

    private var policyTerms: String? = null
    private var _binding: FragmentPrivacyTermsPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrivacyTermsPageBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        policyTerms = arguments?.getString(ARG_TYPE)
        val privacyTermsViewModel =
            ViewModelProvider(this).get(PrivacyTermsViewModel::class.java)
        binding.privacyTermsText.text = privacyTermsViewModel
            .loadAssetTextFile(requireContext(), policyTerms.toString())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}