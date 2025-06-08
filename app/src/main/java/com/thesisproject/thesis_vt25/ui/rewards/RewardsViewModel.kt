package com.thesisproject.thesis_vt25.ui.rewards

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesisproject.thesis_vt25.data.model.Reward
import com.thesisproject.thesis_vt25.data.model.User
import com.thesisproject.thesis_vt25.data.model.UserStore
import com.thesisproject.thesis_vt25.data.repository.LocalRepository
import com.thesisproject.thesis_vt25.data.repository.RemoteRepository
import com.thesisproject.thesis_vt25.data.repository.RemoteRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RewardsViewModel : ViewModel() {

    var user : LiveData<User?> = UserStore.getUser()

    fun getRewards(): ArrayList<Reward>{
        //remoteRepository.getRewards()
        val rewardsList = ArrayList<Reward>()
        for (i in 1..5){
            rewardsList.add(Reward("Coupon ${i *10}% off!","Description $i",100 *i))
        }

        return rewardsList
    }
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