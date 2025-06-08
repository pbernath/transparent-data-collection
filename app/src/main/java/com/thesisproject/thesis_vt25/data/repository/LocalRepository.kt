package com.thesisproject.thesis_vt25.data.repository

import com.thesisproject.thesis_vt25.data.model.Contract
import com.thesisproject.thesis_vt25.data.model.ContractStore
import com.thesisproject.thesis_vt25.data.model.User
import com.thesisproject.thesis_vt25.data.model.UserStore

class LocalRepository {
    fun cacheAvailableContracts(contractList: ArrayList<Contract>){
        ContractStore.setContracts("availableContracts", contractList)
    }
    fun cacheUserInfo(user: User){
        UserStore.setUser(user)
    }

    fun cacheOnboarded(bool: Boolean) {
        UserStore.setOnboarding(true)
    }

    fun cacheReminderDismissed(bool: Boolean) {
        UserStore.setPrivacyReminderDismissed(true)
    }

    fun cacheContractInteraction(contractId: String, action: String){
        when(action){
            "accept" -> ContractStore.removeAvailableContractById(contractId)
            "interrupt" -> ContractStore.removeAcceptedContractById(contractId)
        }
    }

    fun cacheInteractedContracts(contractList: ArrayList<Contract>){
        val ignoredList = ArrayList<Contract>()
        val acceptedList = ArrayList<Contract>()
        val pastList = ArrayList<Contract>()
        for (contract in contractList){
            if (contract.ignored == true){
                ignoredList.add(contract)
                continue
            }
            if (contract.finished != null){
                pastList.add(contract)
                continue
            }else {
                acceptedList.add(contract)
            }
        }
        ContractStore.setContracts("ignoredContracts", ignoredList)
        ContractStore.setContracts("acceptedContracts", acceptedList)
        ContractStore.setContracts("pastContracts", pastList)
    }


}


