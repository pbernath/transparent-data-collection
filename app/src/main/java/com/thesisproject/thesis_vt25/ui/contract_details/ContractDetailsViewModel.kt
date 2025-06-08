package com.thesisproject.thesis_vt25.ui.contract_details

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.thesisproject.thesis_vt25.App
import com.thesisproject.thesis_vt25.data.model.Contract
import com.thesisproject.thesis_vt25.data.model.ContractStore
import com.thesisproject.thesis_vt25.data.repository.LocalRepository
import com.thesisproject.thesis_vt25.data.repository.RemoteRepository
import com.thesisproject.thesis_vt25.data.repository.RemoteRepositoryImpl
import kotlinx.coroutines.launch

class ContractDetailsViewModel(application: Application, savedStateHandle: SavedStateHandle)
    : AndroidViewModel(application) {

    private val appScope = (application as App).applicationScope


    val remoteRepository : RemoteRepository = RemoteRepositoryImpl()
    val localRepository : LocalRepository = LocalRepository()
    var contractId: String = savedStateHandle.get<String>("contractId")
        ?: throw IllegalStateException("Contract id missing")

    val currentContract: Contract = ContractStore.getById("availableContracts", contractId)
        ?: ContractStore.getById("acceptedContracts", contractId)
                ?: ContractStore.getById("pastContracts", contractId)
                ?: ContractStore.getById("ignoredContracts", contractId)
                ?: throw IllegalStateException("Cause: Trying to display the details of a " +
                "contract without an id.")
    private var _title: String = currentContract.title
    val title get() = _title
    private var _overview: String = currentContract.overview
    val overview get() = _overview
    private var _description: String = currentContract.description.toString()
    val description get() = _description
    private var _reward: Int = currentContract.reward
    val reward get() = _reward
    private var _start: String = currentContract.start.toString()
    val start get() = _start
    private var _end: String = currentContract.end.toString()
    val end get() = _end
    private var _permissions: List<String> = currentContract.permissions
    val permissions get() = _permissions
    private var _organization: String = currentContract.organization
    val organization get() = _organization


    private val _isAccepted = MutableLiveData<Boolean>()
    val isAccepted: LiveData<Boolean> get() = _isAccepted

    private val _isPast = MutableLiveData<Boolean>()
    val isPast: LiveData<Boolean> get() = _isPast

    init {
        setCurrentContractState(currentContract)

    }

    fun handleContractInteraction(action: String, id : String?){
        appScope.launch {
            try {
                if(id != null){
                    remoteRepository.handleContractInteraction(id, action)
                    when (action) {
                        "interrupt" -> updateInteractedContracts()
                        "ignored" -> updateAvailableContracts()
                        "accept" -> {
                            updateAvailableContracts()
                            updateInteractedContracts()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ContractDetailsVM", "Failed to handle contract interaction", e)
            }
        }
    }
    suspend fun updateAvailableContracts(){
        try {
            remoteRepository.getAvailableContracts()?.let {
                localRepository.cacheAvailableContracts(it)
            }
        } catch (e: Exception) {
            Log.e("ContractDetailsVM", "Failed to update available contracts", e)
        }
    }
    suspend fun updateInteractedContracts(){
        try {
            remoteRepository.getInteractedContracts()?.let {
                localRepository.cacheInteractedContracts(it)
            }
        } catch (e: Exception) {
            Log.e("ContractDetailsVM", "Failed to update interacted contracts", e)
        }

    }
    fun setCurrentContractState(contract: Contract) {
        if (currentContract.finished != null) {
            _isPast.value = true
            return
        }
        if (currentContract.accepted != null) {
            _isAccepted.value = contract.accepted == true
        }
    }

    fun updateAcceptance() {
        if(_isAccepted.value == true){
            _isPast.value = true
            localRepository.cacheContractInteraction(currentContract.id, "interrupt")
            handleContractInteraction("interrupt", currentContract.interactionId)
            Log.d("Contract Stopped", "This contract has been interrupted.")
        }else{
            _isAccepted.value = true
            localRepository.cacheContractInteraction(currentContract.id, "accept")
            handleContractInteraction("accept", currentContract.id)
        }
    }
}