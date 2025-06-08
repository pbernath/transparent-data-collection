package com.thesisproject.thesis_vt25.data.model

data class User(val id: String, val email: String, val credits: Int, val displayName: String, val onboarded: Boolean, val privacyReminderDismissed: Boolean)
