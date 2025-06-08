package com.thesisproject.thesis_vt25.ui.my_contracts

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.thesisproject.thesis_vt25.common.utils.UiUtils
import com.thesisproject.thesis_vt25.data.model.Contract
import com.thesisproject.thesis_vt25.data.model.ContractStore
import com.thesisproject.thesis_vt25.data.repository.LocalRepository
import com.thesisproject.thesis_vt25.data.repository.RemoteRepository
import com.thesisproject.thesis_vt25.data.repository.RemoteRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyContractsViewModel : ViewModel() {

    val uiUtils = UiUtils()

    val rawAcceptedContracts: LiveData<List<Contract>> = ContractStore.getContractsLiveData("acceptedContracts")

    val acceptedContractList: LiveData<List<Contract>> = rawAcceptedContracts.map { contracts ->
        uiUtils.convertToContractListItems(contracts)
    }

    val rawPastContracts: LiveData<List<Contract>> = ContractStore.getContractsLiveData("pastContracts")

    val pastContractList: LiveData<List<Contract>> = rawPastContracts.map { contracts ->
        uiUtils.convertToContractListItems(contracts)
    }

    val remoteRepository: RemoteRepository = RemoteRepositoryImpl()
    val localRepository: LocalRepository = LocalRepository()


    fun getMyContracts(onResult: (ArrayList<Contract>) -> Unit, onError: (Any?) -> Unit) {
        viewModelScope.launch {
            try {
                val myContracts = withContext(Dispatchers.IO) {
                    remoteRepository.getInteractedContracts() // Your suspend function
                }

                onResult(myContracts!!)

            } catch (e: NoSuchElementException) {
                Log.w("MyContractsViewModel", "No contracts found", e)
                onError("No contracts available.")

            } catch (e: Exception) {
                Log.e("MyContractsViewModel", "Error fetching contracts", e)
                onError("Error: ${e.message ?: "Unknown error"}")
            }
        }
    }

    fun updateMyContracts(){

        getMyContracts(
            onResult = { result ->
                localRepository.cacheInteractedContracts(result)
            },
            onError = { error ->
                Log.e("MyContractsViewModel", "Error loading contracts: $error")
            }
        )
    }

}

