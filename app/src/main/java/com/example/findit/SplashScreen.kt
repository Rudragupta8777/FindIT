package com.example.findit

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Start both operations simultaneously
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
                    Toast.makeText(
                        this@SplashScreen,
                        "Backend Connected ✅",
                        Toast.LENGTH_SHORT
                    ).show()
                    isBackendConnected = true
                    checkAndProceed()
                } else {
                    Log.w(TAG, "Backend Not Connected ❌ : ${response.code()}")
                    Toast.makeText(
                        this@SplashScreen,
                        "Backend Not Connected ❌",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Don't proceed to next screen
                }
            } catch (e: Exception) {
                Log.e(TAG, "Backend Not Connected ❌", e)
                Toast.makeText(
                    this@SplashScreen,
                    "Backend Not Connected ❌ : ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
                // Don't proceed to next screen
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
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}