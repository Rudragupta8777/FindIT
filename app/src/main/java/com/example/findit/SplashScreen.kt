package com.example.findit

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.findit.objects.RetrofitInstance
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {

    private var isBackendConnected = false
    private var isMinimumTimeElapsed = false
    private val minimumSplashTime = 2000L // 2 seconds

    private lateinit var errorMessageTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Init views
        errorMessageTextView = findViewById(R.id.error_message)

        // Start both operations
        initializeRetrofit()
        startMinimumTimeCounter()
    }

    private fun initializeRetrofit() {
        Log.d(TAG, "RetrofitInstance initialized: ${RetrofitInstance.javaClass.name}")
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.publicUserApi.serverStatus()
                if (response.isSuccessful && response.body() != null) {
                    Log.d(TAG, "Backend Connected ✅")
                    isBackendConnected = true
                    checkAndProceed()
                } else {
                    Log.w(TAG, "Backend Not Connected ❌ : ${response.code()}")
                    showErrorMessage()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Backend Not Connected ❌", e)
                showErrorMessage()
            }
        }
    }

    private fun startMinimumTimeCounter() {
        Handler(Looper.getMainLooper()).postDelayed({
            isMinimumTimeElapsed = true
            checkAndProceed()
        }, minimumSplashTime)
    }

    private fun checkAndProceed() {
        if (isBackendConnected && isMinimumTimeElapsed) {
            proceedToNextScreen()
        }
    }

    private fun proceedToNextScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showErrorMessage() {
        runOnUiThread {
            errorMessageTextView.text = "Unable to connect...\nPlease check your internet connection."
            errorMessageTextView.visibility = TextView.VISIBLE
        }
    }
}
