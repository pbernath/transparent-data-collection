package com.thesisproject.thesis_vt25.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.thesisproject.thesis_vt25.MainActivity
import com.thesisproject.thesis_vt25.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        if(authViewModel.isLoggedIn()){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        authViewModel.loginSuccess.observe(this) {
            if (it) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        authViewModel.loginError.observe(this) { error ->
            error?.let {
                Toast.makeText(this, "Error: $it", Toast.LENGTH_LONG).show()
                authViewModel.clearError()
            }
        }

        binding.buttonLogin.setOnClickListener {
            authViewModel.login(binding.inputEmail.text.toString(),
                binding.inputPassword.text.toString())
        }

        binding.textLoginNotRegistered.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}
