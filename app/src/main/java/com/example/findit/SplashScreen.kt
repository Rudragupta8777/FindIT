package com.example.findit

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.findit.MainActivity
import com.example.findit.objects.RetrofitInstance
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initializeRetrofit()
        setContentView(R.layout.activity_splash_screen)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }

    private fun initializeRetrofit() {
        // Simply referencing RetrofitInstance will initialize it
        Log.d(TAG, "RetrofitInstance initialized: ${RetrofitInstance.javaClass.name}")
        lifecycleScope.launch {
            try {

                // Make the API call
                val response = RetrofitInstance.publicUserApi.serverStatus()
                if (response.isSuccessful && response.body() != null) {
                    Log.d(TAG, "Backend Connected ✅")
                    Toast.makeText(
                        this@SplashScreen,
                        "Backend Connected ✅",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.w(TAG, "Backend Not Connected ❌ : ${response.code()}")
                    // Stay on login screen - backend login failed
                    Toast.makeText(
                        this@SplashScreen,
                        "Backend Not Connected ❌",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Backend Not Connected ❌", e)
                Toast.makeText(
                    this@SplashScreen,
                    "Backend Not Connected ❌ : ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}