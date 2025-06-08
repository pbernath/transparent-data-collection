package com.thesisproject.thesis_vt25

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.thesisproject.thesis_vt25.data.model.ContractStore
import com.thesisproject.thesis_vt25.data.model.UserStore
import com.thesisproject.thesis_vt25.data.repository.LocalRepository
import com.thesisproject.thesis_vt25.data.repository.RemoteRepository
import com.thesisproject.thesis_vt25.data.repository.RemoteRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class App : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        val remoteRepository: RemoteRepository = RemoteRepositoryImpl()
        val localRepository: LocalRepository = LocalRepository()
        val auth = FirebaseAuth.getInstance()
        // Listen for changes in auth state
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Log.d("Application Create", "This is the on create")
                preloadContracts(remoteRepository, localRepository)
                preloadUserInfo(remoteRepository, localRepository)
            } else {
                ContractStore.clearAll()
                UserStore.clear()
            }
        }
    }

    private fun preloadContracts(remoteRepository: RemoteRepository, localRepository: LocalRepository) {
        applicationScope.launch {
            try {
                val interactedContracts = remoteRepository.getInteractedContracts()

                val availableContracts = remoteRepository.getAvailableContracts()

                if(availableContracts != null){
                    localRepository.cacheAvailableContracts(availableContracts)
                }
                if(interactedContracts != null){
                    localRepository.cacheInteractedContracts(interactedContracts)
                }

            } catch (e: Exception) {
                Log.e("AppInit", "Failed to fetch contracts: ${e.message}")
            }
        }
    }
    private fun preloadUserInfo(remoteRepository: RemoteRepository, localRepository: LocalRepository) {
        applicationScope.launch {
            try {
                val userInfo = remoteRepository.getUserInfo()
                if(userInfo != null){
                    localRepository.cacheUserInfo(userInfo)
                }

            } catch (e: Exception) {
                Log.e("AppInit", "Failed to fetch user: ${e.message}")
            }
        }
    }
}