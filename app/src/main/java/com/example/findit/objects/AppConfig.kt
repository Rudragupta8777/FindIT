package com.example.findit.objects

import android.util.Log

object AppConfig {
    // This will be initialized once and remain in memory
    val apiKey: String by lazy {
        try {
            com.example.findit.BuildConfig.API_KEY.also {
                Log.d("AppConfig", "API Key loaded")
            }
        } catch (e: Exception) {
            Log.e("AppConfig", "Failed to load API key", e)
            "" // Fallback empty string
        }
    }

    // Call this in your Application class
    fun init() {
        // Triggers lazy initialization
        apiKey
    }
}