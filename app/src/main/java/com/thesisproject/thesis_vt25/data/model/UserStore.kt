package com.thesisproject.thesis_vt25.data.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object UserStore {
    private var user = MutableLiveData<User?>()

    fun setUser(user: User) {
        this.user.postValue(user)
    }

    fun getUser(): LiveData<User?> {
        return user
    }

    fun getOnboardingFinished(): Boolean {
        return (user.value?.onboarded == true)
    }
    fun getPrivacyReminderDismissed(): Boolean {
        return (user.value?.privacyReminderDismissed == true)
    }
    fun setPrivacyReminderDismissed(bool: Boolean) {
        user.postValue(User(getId().toString(), getEmail().toString(),
            getCredits()?: 0, getDisplayName().toString(),
            getOnboardingFinished(),
            bool)
        )
    }

    fun setOnboarding(bool: Boolean){
        user.postValue(User(getId().toString(), getEmail().toString(),
            getCredits()?: 0, getDisplayName().toString(),
            bool,
            getPrivacyReminderDismissed())
        )

    }

    fun getId(): String?{
        return user.value?.id
    }

    fun getCredits(): Int? {
        return user.value?.credits
    }

    fun getEmail(): String? {
        return user.value?.email
    }

    fun getDisplayName(): String? {
        return user.value?.displayName
    }

    fun clear() {
        user.postValue(null)

    }
}
