package com.thesisproject.thesis_vt25.data.repository

import com.thesisproject.thesis_vt25.data.model.Contract
import com.thesisproject.thesis_vt25.data.model.User

interface RemoteRepository {
    suspend fun getAvailableContracts(): ArrayList<Contract>?
    suspend fun getInteractedContracts(): ArrayList<Contract>?
    suspend fun getUserInfo(): User?
    suspend fun handleContractInteraction(contractId: String, action: String): String
    suspend fun setUserOnboarded()
    suspend fun setDataRequested()
    suspend fun setDeletionRequested()
    suspend fun setReminderDismissed()



}


