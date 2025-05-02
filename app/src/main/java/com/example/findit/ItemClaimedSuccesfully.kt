package com.example.findit

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.findit.databinding.ActivityItemClaimedSuccesfullyBinding

class ItemClaimedSuccesfully : AppCompatActivity() {

    private lateinit var binding: ActivityItemClaimedSuccesfullyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityItemClaimedSuccesfullyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initially hide the text and home button
        binding.textDisplay.visibility = View.INVISIBLE
        binding.btnHome.visibility = View.INVISIBLE

        // Get QR code value if passed
        val qrCodeValue = intent.getStringExtra("QR_CODE_VALUE")

        // Set up the success animation sequence
        setupSuccessAnimation()

        // Set up the home button click listener
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
            finish() // Close this activity
        }
    }

    private fun setupSuccessAnimation() {
        // Play the Lottie animation
        binding.lottieAnimationView.playAnimation()

        // After 2 seconds, stop the animation loop and show the text
        Handler(Looper.getMainLooper()).postDelayed({
            // Stop animation loop
            binding.lottieAnimationView.cancelAnimation()

            // Display the text and home button
            binding.textDisplay.visibility = View.VISIBLE
            binding.btnHome.visibility = View.VISIBLE

            // You can optionally animate the text appearance
            binding.textDisplay.alpha = 0f
            binding.textDisplay.animate().alpha(1f).setDuration(500).start()

            binding.btnHome.alpha = 0f
            binding.btnHome.animate().alpha(1f).setDuration(500).start()

        }, 2000) // 2 seconds delay
    }
}