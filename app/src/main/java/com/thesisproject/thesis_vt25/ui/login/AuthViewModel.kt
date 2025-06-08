package com.thesisproject.thesis_vt25.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel() : ViewModel() {

    var auth = FirebaseAuth.getInstance()

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _loginError = MutableLiveData<String?>()
    val loginError: LiveData<String?> = _loginError

    private val _signupSuccess = MutableLiveData<Boolean>()
    val signupSuccess: LiveData<Boolean> = _signupSuccess

    private val _signupError = MutableLiveData<String?>()
    val signupError: LiveData<String?> = _signupError

    fun login(email : String, password : String){
        if (email.isEmpty() || password.isEmpty()) {
            _loginError.value = "Invalid email and/or password"
            return
        }

        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            _loginSuccess.value = true
        }.addOnFailureListener {
            _loginError.value = it.message
        }
    }

    fun signUp(email : String, password : String, agreed : Boolean){

        if (email.isEmpty() || password.isEmpty()) {
            _signupError.value = "Invalid email and/or password."
            return
        }

        if (!agreed){
            _signupError.value = "You must agree to the Terms and Conditions and the Privacy Policy"
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _signupSuccess.value = true
            }
            .addOnFailureListener {
                _signupError.value = it.message
            }
    }

    fun isLoggedIn() : Boolean{
        return auth.currentUser != null
    }

    fun clearError(){
        _loginError.value = null
        _signupError.value = null
    }
}