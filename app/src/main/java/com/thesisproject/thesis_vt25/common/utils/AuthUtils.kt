package com.thesisproject.thesis_vt25.common.utils

import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.thesisproject.thesis_vt25.ui.login.LoginActivity
class AuthUtils {
    fun logOut(context: Context) {
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // clear back stack
        context.startActivity(intent)
    }
}