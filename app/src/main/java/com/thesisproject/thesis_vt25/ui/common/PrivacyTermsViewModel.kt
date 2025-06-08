package com.thesisproject.thesis_vt25.ui.common

import android.content.Context
import androidx.lifecycle.ViewModel

class PrivacyTermsViewModel : ViewModel() {

    fun loadAssetTextFile(context: Context, filename: String): String {
        return context.assets.open(filename).bufferedReader().use { it.readText() }
    }
}