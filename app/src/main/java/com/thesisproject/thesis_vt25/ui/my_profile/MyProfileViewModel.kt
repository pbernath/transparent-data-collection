package com.thesisproject.thesis_vt25.ui.my_profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesisproject.thesis_vt25.common.utils.removeBrackets
import com.thesisproject.thesis_vt25.common.utils.toReadable
import com.thesisproject.thesis_vt25.data.model.Contract
import com.thesisproject.thesis_vt25.data.model.ContractStore
import com.thesisproject.thesis_vt25.data.model.User
import com.thesisproject.thesis_vt25.data.model.UserStore
import com.thesisproject.thesis_vt25.data.repository.LocalRepository
import com.thesisproject.thesis_vt25.data.repository.RemoteRepository
import com.thesisproject.thesis_vt25.data.repository.RemoteRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyProfileViewModel : ViewModel() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    var user : LiveData<User?> = UserStore.getUser()
    var contracts : LiveData<List<Contract>> = ContractStore.getContractsLiveData("acceptedContracts")

    val remoteRepository: RemoteRepository = RemoteRepositoryImpl()
    val localRepository: LocalRepository = LocalRepository()

    fun getUserInfo(onResult: (User) -> Unit, onError: (Any?) -> Unit) {
        viewModelScope.launch {
            try {
                val user = withContext(Dispatchers.IO) {
                    remoteRepository.getUserInfo() // Your suspend function
                }

                onResult(user!!)

            } catch (e: NoSuchElementException) {
                Log.w("HomeViewModel", "No contracts found", e)
                onError("No contracts available.")

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching contracts", e)
                onError("Error: ${e.message ?: "Unknown error"}")
            }
        }
    }

    fun getPermissionsUsed(): String {
        val contractList: List<Contract> = ContractStore.getContractsLiveData("acceptedContracts").value ?: emptyList()
        var set : MutableSet<String> = mutableSetOf<String>()
        if (contractList.isNotEmpty()) {
            for(contract in contractList){
                if (contract.status.toString().uppercase() == "ACTIVE"){
                    set.addAll(contract.permissions.toReadable())
                }
            }
        }
        return if (set.toString().contains("")) set.toString().replace("[]", "Fictional Company is currently not using any permissions.") else set.toString().removeBrackets()
    }

    fun setDataRequested() {
        applicationScope.launch {
            try {
                remoteRepository.setDataRequested()

            } catch (e: Exception) {
                Log.e("MyProfileViewModel", "Error setting data requested", e)
            }
        }
    }
    fun setDeletionRequested() {
        applicationScope.launch {
            try {
                remoteRepository.setDeletionRequested()

            } catch (e: Exception) {
                Log.e("MyProfileViewModel", "Error setting deletion requested", e)
            }
        }
    }

    fun updateUserInfo(){
        getUserInfo(
            onResult = { result ->
                localRepository.cacheUserInfo(result)
            },
            onError = { error ->
                Log.e("MyProfileViewModel", "Error getting user info: $error")
            }
        )
    }
}