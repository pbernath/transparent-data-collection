package com.thesisproject.thesis_vt25.data.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object ContractStore {
    private val contractLiveDataMap = mutableMapOf<String, MutableLiveData<List<Contract>>>()

    fun setContracts(key: String, newContracts: List<Contract>) {
        val liveData = contractLiveDataMap.getOrPut(key) { MutableLiveData() }
        liveData.postValue(newContracts)
    }

    fun getContractsLiveData(key: String): LiveData<List<Contract>> {
        return contractLiveDataMap.getOrPut(key) { MutableLiveData(emptyList()) }
    }

    fun clear(key: String) {
        contractLiveDataMap[key]?.postValue(emptyList())
    }

    fun clearAll() {
        for (entry in contractLiveDataMap.values) {
            entry.postValue(emptyList())
        }
        contractLiveDataMap.clear()
    }

    fun removeAvailableContractById(contractId: String) {
        val liveData = contractLiveDataMap["availableContracts"]
        liveData?.value?.let { currentList ->
            val updatedList = currentList.filterNot { it.id == contractId }
            liveData.value = updatedList
        }
    }


    fun removeAcceptedContractById(contractId: String){
        val liveData = contractLiveDataMap["acceptedContracts"]
        liveData?.value?.let { currentList ->
            val updatedList = currentList.filterNot { it.id == contractId }
            liveData.value = updatedList
        }
    }

    fun getById(key: String, id: String): Contract? {
        return contractLiveDataMap[key]?.value?.find { it.id == id }
    }
}
