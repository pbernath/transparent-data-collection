package com.thesisproject.thesis_vt25.ui.contract_onboarding

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

class OnboardingViewModel : ViewModel() {
    val remoteRepository: RemoteRepository = RemoteRepositoryImpl()
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val localRepository: LocalRepository = LocalRepository()

    private var _currentIndex = MutableLiveData<Int>().apply {
        value = 0
    }
    var user : LiveData<User?> = UserStore.getUser()

    var previouslyCompleted = user.value?.onboarded == true

    val currentIndex: LiveData<Int> = _currentIndex

    private var _complete = MutableLiveData<Boolean>().apply {
        value = false
    }
    val complete: LiveData<Boolean> = _complete
    fun setUserOnboarded() {
        applicationScope.launch {
            try {
                remoteRepository.setUserOnboarded()

            } catch (e: Exception) {
                Log.e("OnboardingViewModel", "Error updating onboarded", e)
            }
        }
    }

    fun setIndex(index: Int, maxIndex: Int?){
        if (index != maxIndex){
            _currentIndex.value = index
            return
        }else{
            localRepository.cacheOnboarded(true)
            setUserOnboarded()
            _complete.value = true
            Log.d("OnboardingViewModel", "No more pages, onboarding complete")
        }
    }
}