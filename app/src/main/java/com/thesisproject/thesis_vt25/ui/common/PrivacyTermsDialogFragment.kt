package com.thesisproject.thesis_vt25.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.thesisproject.thesis_vt25.R
import com.thesisproject.thesis_vt25.databinding.FragmentPrivacyTermsPageBinding

class PrivacyTermsDialogFragment() : DialogFragment() {
    companion object {
        private const val ARG_TYPE = "policy_type"

        fun newInstance(type: String): PrivacyTermsDialogFragment {
            val fragment = PrivacyTermsDialogFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_TYPE, type)
            }
            return fragment
        }
    }
    private var _binding: FragmentPrivacyTermsPageBinding? = null
    private val binding get() = _binding!!
    private var policyTerms: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        policyTerms = arguments?.getString(ARG_TYPE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrivacyTermsPageBinding.inflate(inflater, container, false)
        val privacyTermsViewModel =
            ViewModelProvider(this).get(PrivacyTermsViewModel::class.java)
        binding.privacyTermsText.text = privacyTermsViewModel
            .loadAssetTextFile(requireContext(), policyTerms.toString())

        val root: View = binding.root
        return root
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                (resources.displayMetrics.heightPixels * 0.7).toInt()
            )
            setBackgroundDrawableResource(R.drawable.custom_dialog)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}