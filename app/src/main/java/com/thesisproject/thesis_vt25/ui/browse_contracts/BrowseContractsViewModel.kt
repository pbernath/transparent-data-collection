package com.thesisproject.thesis_vt25.ui.browse_contracts


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

class BrowseContractsViewModel : ViewModel() {

    val uiUtils = UiUtils()

    val rawContracts: LiveData<List<Contract>> = ContractStore.getContractsLiveData("availableContracts")

    val contractList: LiveData<List<Contract>> = rawContracts.map { contracts ->
        uiUtils.convertToContractListItems(contracts)
    }
    val localRepository: LocalRepository = LocalRepository()
    val remoteRepository: RemoteRepository = RemoteRepositoryImpl()

    fun getContracts(onResult: (ArrayList<Contract>) -> Unit, onError: (Any?) -> Unit) {
        viewModelScope.launch {
            try {
                val availableContracts = withContext(Dispatchers.IO) {
                    remoteRepository.getAvailableContracts() // Your suspend function
                }

                onResult(availableContracts!!)

            } catch (e: NoSuchElementException) {
                Log.w("BrowseContractsViewModel", "No contracts found", e)
                onError("No contracts available.")

            } catch (e: Exception) {
                Log.e("BrowseContractsViewModel", "Error fetching contracts", e)
                onError("Error: ${e.message ?: "Unknown error"}")
            }
        }
    }

    fun updateAvailableContracts(){
        Log.d("Fragment", "Reloading data")
        getContracts(
            onResult = { result ->
                localRepository.cacheAvailableContracts(result)
            },
            onError = { error ->
                Log.e("BrowseContractsViewModel", "Error loading contracts: $error")
            }
        )
    }
    
}