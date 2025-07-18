package com.example.findit

import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.findit.databinding.ActivityItemClaimedSuccesfullyBinding

class ItemClaimedSuccesfully : AppCompatActivity() {

    private lateinit var binding: ActivityItemClaimedSuccesfullyBinding
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = ActivityItemClaimedSuccesfullyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initially hide the text and home button
        binding.textDisplay.visibility = View.INVISIBLE
        binding.btnHome.visibility = View.INVISIBLE

        // Play success sound and run animation
        setupSuccessAnimation()

        binding.btnHome.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupSuccessAnimation() {
        // Speed up the Lottie animation
        binding.lottieAnimationView.setSpeed(1.5f) // 🔥 Adjust speed as needed
        binding.lottieAnimationView.playAnimation()

        // Play the success sound
        mediaPlayer = MediaPlayer.create(this, R.raw.success_sound)
        mediaPlayer.setOnCompletionListener {
            // After sound completes, stop animation and show UI
            binding.lottieAnimationView.cancelAnimation()
            showSuccessUI()
        }
        mediaPlayer.start()
    }


    private fun showSuccessUI() {
        binding.textDisplay.visibility = View.VISIBLE
        binding.btnHome.visibility = View.VISIBLE

        binding.textDisplay.alpha = 0f
        binding.textDisplay.animate().alpha(1f).setDuration(500).start()

        binding.btnHome.alpha = 0f
        binding.btnHome.animate().alpha(1f).setDuration(500).start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }
}
