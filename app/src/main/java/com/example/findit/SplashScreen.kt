package com.example.findit

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.findit.data.VersionHistory
import com.example.findit.objects.AppConfig
import com.example.findit.objects.RetrofitInstance
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {

    private var isBackendConnected = false
    private var isMinimumTimeElapsed = false
    private val minimumSplashTime = 2000L // 2 seconds

    private lateinit var errorMessageTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppConfig.init()
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
        Log.d(TAG, "Initializing Retrofit...")
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.publicUserApi.serverStatus(AppConfig.apiKey)

                when {
                    response.isSuccessful -> {
                        response.body()?.let { versionHistory ->
                            handleVersionCheck(versionHistory)
                        } ?: run {
                            Log.w(TAG, "Response body is null")
                        }
                    }
                    else -> {
                        Log.w(TAG, "Server error: ${response.code()} - ${response.message()}")

                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Network error: ${e.message}", e)

            }
        }
    }

    private fun handleVersionCheck(versionHistory: VersionHistory) {
        Log.d(TAG, "Server version: ${versionHistory.versionCode}, App version: ${BuildConfig.VERSION_CODE}")

        when {
            versionHistory.versionCode > BuildConfig.VERSION_CODE && versionHistory.forceUpdate -> {
                // Force update required
                runOnUiThread {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(versionHistory.url)).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                        finish() // Close the app to force update
                        Toast.makeText(this,"Please update your app.", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(this,"Please update your app", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            versionHistory.versionCode > BuildConfig.VERSION_CODE -> {
                // Optional update available (not forced)
                Log.i(TAG, "New version available but not forced")
                isBackendConnected = true
                checkAndProceed()
            }
            else -> {
                // Version is OK
                isBackendConnected = true
                checkAndProceed()
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
