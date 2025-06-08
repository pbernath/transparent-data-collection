package com.thesisproject.thesis_vt25.ui.home


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesisproject.thesis_vt25.data.model.User
import com.thesisproject.thesis_vt25.data.model.UserStore
import com.thesisproject.thesis_vt25.data.repository.LocalRepository
import com.thesisproject.thesis_vt25.data.repository.RemoteRepository
import com.thesisproject.thesis_vt25.data.repository.RemoteRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    val remoteRepository: RemoteRepository = RemoteRepositoryImpl()
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val localRepository: LocalRepository = LocalRepository()

    private val _text = MutableLiveData<String>().apply {
        value = "This is Home which would contain the main functions of the application."
    }
    val text: LiveData<String> = _text

    val user : LiveData<User?> = UserStore.getUser()

    fun setPrivacyReminderDismissed() {
        applicationScope.launch {
            try {
                localRepository.cacheReminderDismissed(true)
                remoteRepository.setReminderDismissed()

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error updating dismissed", e)
            }
        }
    }


}

