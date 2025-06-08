package com.thesisproject.thesis_vt25.ui.contract_details


import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.thesisproject.thesis_vt25.common.utils.UiUtils
import com.thesisproject.thesis_vt25.common.utils.promptOnboarding
import com.thesisproject.thesis_vt25.common.utils.removeBrackets
import com.thesisproject.thesis_vt25.common.utils.toReadable
import com.thesisproject.thesis_vt25.databinding.FragmentContractDetailsBinding

class ContractDetailsFragment : Fragment() {
    private var _binding: FragmentContractDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var builder: AlertDialog.Builder
    private lateinit var contractDetailsViewModel: ContractDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContractDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        buildPermissionLauncher()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        promptOnboarding(findNavController())

        val uiUtils = UiUtils()
        contractDetailsViewModel = ViewModelProvider(this)[ContractDetailsViewModel::class.java]
        binding.textContractDetailsTitle.text = contractDetailsViewModel.title
        binding.textContractDetailsReward.text = contractDetailsViewModel.reward.toString()
        binding.textContractDetailsOverview.text = contractDetailsViewModel.overview
        binding.textContractDetailsDescription.text = contractDetailsViewModel.description
        binding.textContractDetailsPermissions.text = contractDetailsViewModel.permissions.toReadable().toString().removeBrackets()
        binding.textContractDetailsStartDate.text = contractDetailsViewModel.start
        binding.textContractDetailsEndDate.text = contractDetailsViewModel.end
        binding.textContractDetailsOrganization.text = contractDetailsViewModel.organization

        contractDetailsViewModel.isAccepted.observe(viewLifecycleOwner) { accepted ->
            // Update UI or logic based on accepted status
            changeButtonText(accepted)
        }

        contractDetailsViewModel.isPast.observe(viewLifecycleOwner) { past ->
            // Update UI based on whether it's a past contract
            if (past) {
                binding.buttonAcceptContract.visibility = View.GONE
            }
        }

        binding.buttonAcceptContract.setOnClickListener {
            val action = if(contractDetailsViewModel.isAccepted.value == true) "STOP" else "ACCEPT"
            //If the contract has been accepted, show STOP in the alert and vice versa
            uiUtils.contractAlertBuilder(requireContext(), action, {
                if (action == "ACCEPT"){
                    startPermissionRequest(contractDetailsViewModel.permissions)
                }
                else{
                    contractDetailsViewModel.updateAcceptance()
                    findNavController().popBackStack()
                    Toast.makeText(this.context, "Contract interrupted", Toast.LENGTH_LONG).show()
                }
            }, null, contractDetailsViewModel.permissions.toReadable())

        }
    }

    fun changeButtonText(isAccepted: Boolean){
        val buttonText = if(isAccepted) "Stop contract" else "Accept contract"
        binding.buttonAcceptContract.text = buttonText
    }

    fun startPermissionRequest(permissions : List<String>){

        var permissionsToRequest = checkPermissions(permissions)
        if(permissionsToRequest.isNotEmpty()){
            Log.d("Perms", "requesting")
            requestPermissionsLauncher.launch(permissionsToRequest)
        } else{
            Log.d("Permissions", "Proceed with task, and pop back to browse contracts")
            contractDetailsViewModel.updateAcceptance()
            findNavController().popBackStack()
            Toast.makeText(this.context, "Contract accepted", Toast.LENGTH_LONG).show()
        }
    }

    fun buildPermissionLauncher(){
        requestPermissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                var notAcceptedPermissions = mutableListOf<String>()
                it.forEach {
                    if (it.value) {
                        Log.d("Permission", "${it.key} permission granted")
                    } else {
                        Log.d("Permission", "${it.key} permission denied")
                        notAcceptedPermissions.add(it.key)
                        // Permission denied, show a message or handle accordingly
                    }
                }

                if (notAcceptedPermissions.isEmpty()){
                    contractDetailsViewModel.updateAcceptance() //permissions accepted,
                    findNavController().popBackStack()
                    Toast.makeText(this.context, "Contract accepted", Toast.LENGTH_LONG).show()
                    Log.d("Permissions", "Proceed with task, and pop back to browse contracts")
                } else{
                    buildDialog(notAcceptedPermissions)
                }
            }
    }

    fun buildDialog(notAcceptedPermissions: List<String>){
        val perms = notAcceptedPermissions.toReadable()
        builder = AlertDialog.Builder(context)
        builder.setTitle("Contract cannot be accepted")
            .setMessage("The following permissions were not granted:\n\n${perms.toString().removeBrackets()}\n\nEnable " +
                    "those permissions in the settings in order to accept this contract.")
            .setPositiveButton("Open settings") { dialog, which ->
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", context?.packageName, null)
                })
            }
            .setNegativeButton("Cancel") { dialog, which ->
                Toast.makeText(this.context, "The contract cannot be accepted due to lack of permissions.", Toast.LENGTH_LONG).show()
            }.setOnCancelListener {
                Toast.makeText(this.context, "The contract cannot be accepted due to lack of permissions.", Toast.LENGTH_LONG).show()
            }.create().show()
    }
    fun checkPermissions(contractPermissions: List<String>) : Array<String>{
        var permissionsToBeRequested = mutableListOf<String>()
        utilToManifestPermissions(contractPermissions).forEach {
            if (ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED){
                Log.d("Permissions", "$it not granted")
                permissionsToBeRequested.add(it)
            }else{
                Log.d("Permissions", "$it granted")
            }
        }
        return permissionsToBeRequested.toTypedArray()
    }

    fun utilToManifestPermissions(contractPermissions: List<String>) : List<String>{
        return contractPermissions.map { "android.permission.$it" }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}