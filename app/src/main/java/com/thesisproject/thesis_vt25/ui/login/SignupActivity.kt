package com.thesisproject.thesis_vt25.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.thesisproject.thesis_vt25.MainActivity
import com.thesisproject.thesis_vt25.databinding.ActivitySignupBinding
import com.thesisproject.thesis_vt25.ui.common.PrivacyTermsDialogFragment

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        authViewModel.signupSuccess.observe(this) {
            if (it) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        authViewModel.signupError.observe(this) { error ->
            error?.let {
                Toast.makeText(this, "Error: $it", Toast.LENGTH_LONG).show()
                authViewModel.clearError()
            }
        }
        binding.buttonSignUp.setOnClickListener {
            authViewModel.signUp(binding.inputEmail.text.toString(),
                binding.inputPassword.text.toString(),
                binding.checkboxTermsAndConditions.isChecked)
        }
        setupClickablePolicyLinks()


    }

    private fun setupClickablePolicyLinks() {
        val termsText = "Terms and Conditions"
        val privacyText = "Privacy Policy"
        val fullText = "Read our $termsText and $privacyText"

        val spannable = SpannableString(fullText)

        val termsStart = fullText.indexOf(termsText)
        val privacyStart = fullText.indexOf(privacyText)

        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val dialog = PrivacyTermsDialogFragment.newInstance("terms_of_service.txt")
                dialog.show(supportFragmentManager, "TermsDialog")
            }
        }, termsStart, termsStart + termsText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val dialog = PrivacyTermsDialogFragment.newInstance("privacy_policy.txt")
                dialog.show(supportFragmentManager, "PolicyDialog")
            }
        }, privacyStart, privacyStart + privacyText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.termsLinksText.text = spannable
        binding.termsLinksText.movementMethod = LinkMovementMethod.getInstance()
        binding.termsLinksText.highlightColor = Color.TRANSPARENT
    }

}
