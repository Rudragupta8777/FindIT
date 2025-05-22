package com.example.findit

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView

class DevelopersTeam : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_developers_team)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Initialize UI elements
        setupNavigation()
        setupSocialLinks()

        val back = findViewById<ImageView>(R.id.btn_back)
        back.setOnClickListener {
            val intent = Intent(this, MyProfile::class.java)
            startActivity(intent)
        }

        val home = findViewById<ImageView>(R.id.btn_home)
        home.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }
    }

    private fun setupNavigation() {
        // Set up back button
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        // Set up home button
        val btnHome = findViewById<ImageView>(R.id.btn_home)
        btnHome.setOnClickListener {
            // Navigate to your home activity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun setupSocialLinks() {
        // Find all buttons
        val btnLinkedinPratham = findViewById<CardView>(R.id.btn_linkedin_pratham)
        val btnGithubPratham = findViewById<CardView>(R.id.btn_github_pratham)
        val btnLinkedinRudra = findViewById<CardView>(R.id.btn_linkedin_rudra)
        val btnGithubRudra = findViewById<CardView>(R.id.btn_github_rudra)

        // Apply ripple effect to all buttons
        applyButtonEffect(btnLinkedinPratham)
        applyButtonEffect(btnGithubPratham)
        applyButtonEffect(btnLinkedinRudra)
        applyButtonEffect(btnGithubRudra)

        // Define the social profile URLs
        // Replace these URLs with the actual profile URLs
        val linkedinPratham = "https://www.linkedin.com/in/pratham-khanduja/"
        val githubPratham = "https://github.com/pratham-developer"
        val linkedinRudra = "https://www.linkedin.com/in/rudra-gupta-36827828b/"
        val githubRudra = "https://github.com/Rudragupta8777"

        // Set click listeners for Pratham's social links
        btnLinkedinPratham.setOnClickListener {
            animateButtonClick(it)
            openUrl(linkedinPratham)
        }

        btnGithubPratham.setOnClickListener {
            animateButtonClick(it)
            openUrl(githubPratham)
        }

        // Set click listeners for Rudra's social links
        btnLinkedinRudra.setOnClickListener {
            animateButtonClick(it)
            openUrl(linkedinRudra)
        }

        btnGithubRudra.setOnClickListener {
            animateButtonClick(it)
            openUrl(githubRudra)
        }
    }

    // Helper method to open URLs
    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    // Helper method to apply button-like effect
    private fun applyButtonEffect(view: CardView) {
        // Make the view clickable (important for ripple effect)
        view.isClickable = true
        view.isFocusable = true

        // Set foreground to ripple drawable (pre-defined in Android)
        // Instead of using the theme's selectableItemBackground
        view.foreground = getDrawable(R.drawable.button_ripple)
    }

    // Helper method to add click animation
    private fun animateButtonClick(view: android.view.View) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }
}