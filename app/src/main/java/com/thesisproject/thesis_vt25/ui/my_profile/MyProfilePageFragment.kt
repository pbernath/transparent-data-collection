package com.thesisproject.thesis_vt25.ui.my_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thesisproject.thesis_vt25.common.utils.confirmationAlertBuilder
import com.thesisproject.thesis_vt25.common.utils.removeBrackets
import com.thesisproject.thesis_vt25.databinding.FragmentProfilePageBinding

class MyProfilePageFragment : Fragment() {

    private var _binding: FragmentProfilePageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profileViewModel =
            ViewModelProvider(this).get(MyProfileViewModel::class.java)

        profileViewModel.updateUserInfo()

        profileViewModel.user.observe(viewLifecycleOwner) {
            binding.textProfileEmail.text = profileViewModel.user.value?.email
            binding.textProfileName.text = profileViewModel.user.value?.displayName
            binding.textProfileCredits.text = profileViewModel.user.value?.credits.toString()
            binding.textProfilePermissions.text = profileViewModel.getPermissionsUsed().removeBrackets()
            binding.containerProfilePermissions.visibility =
                if (profileViewModel.user.value?.onboarded == true) View.VISIBLE else View.GONE

            binding.checkboxTerms.isChecked = true
            binding.checkboxPrivacy.isChecked = true
        }

        binding.buttonProfileOnboarding.setOnClickListener {
            val message = "This will launch the tutorial again."
            confirmationAlertBuilder(requireContext(),
                message = message,
                negativeText = "Cancel",
                positiveText =  "OK",
                neutralText = null,
                acceptAction = {
                findNavController().navigate(MyProfileFragmentDirections.actionNavMyProfileToNavOnboarding())
                               },
                denyAction = null)
        }

        binding.buttonProfileSavePreferences.setOnClickListener {
            if (!binding.checkboxPrivacy.isChecked || !binding.checkboxTerms.isChecked){
                val message = "Using Fictional Company requires agreement to our Privacy Policy and Terms & Conditions. If you choose not to agree, you may delete your account at any time."
                confirmationAlertBuilder(
                    requireContext(),
                    message = message,
                    negativeText = "",
                    positiveText = "",
                    neutralText = "OK",
                    acceptAction = {
                        binding.checkboxTerms.isChecked = true
                        binding.checkboxPrivacy.isChecked = true
                    },
                    denyAction = null
                )
            }
        }

        binding.buttonProfileOnboarding.setOnClickListener {
            val message = "This will launch the tutorial again."
            confirmationAlertBuilder(
                requireContext(),
                message = message,
                negativeText = "Cancel",
                positiveText = "OK",
                neutralText = null,
                acceptAction = {
                    findNavController().navigate(MyProfileFragmentDirections.actionNavMyProfileToNavOnboarding())
                },
                denyAction = null
            )
        }
        binding.buttonProfileDataDeletionRequest.setOnClickListener {
            val message =
                "Unfortunately, the only way to delete your data is through deleting your account. The button to do so can be found below the one you just pressed."
            confirmationAlertBuilder(
                requireContext(),
                message = message,
                negativeText = "",
                positiveText = "",
                neutralText = "OK",
                acceptAction = {
                    // Nothing
                },
                denyAction = null
            )
        }
        binding.buttonProfileDataRequest.setOnClickListener {
                val message ="This will send us a request for all of your personal data. Once processed we will send this to your email.\n\nDo you wish to proceed?"

                confirmationAlertBuilder(
                    requireContext(),
                    message = message,
                    negativeText = "No",
                    positiveText = "Yes",
                    neutralText = null,

                    acceptAction = {
                        profileViewModel.setDataRequested()
                    },
                    denyAction = null
                )
            }
        binding.buttonProfileDeletionRequest.setOnClickListener {
                val message =
                    "This will send us a request to delete your account. This action is irreversible.\n\nDo you wish to proceed?"
                confirmationAlertBuilder(
                    requireContext(),
                    message = message,
                    negativeText = "No",
                    positiveText = "Yes",
                    neutralText = null,
                    acceptAction = {
                        profileViewModel.setDeletionRequested()
                    },
                    denyAction = null
                )
            }



        swipeRefreshLayout = binding.swipeRefreshLayoutProfilePage
        swipeRefreshLayout.setOnRefreshListener {
                // Reload data
            profileViewModel.updateUserInfo()
            swipeRefreshLayout.isRefreshing = false // Stop spinner after reload
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

