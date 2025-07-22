package com.example.findit

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView

class DevelopersTeam : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developers_team)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.navigation_bar_color)
            window.statusBarColor = ContextCompat.getColor(this, R.color.header)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        setupNavigation()
        setupSocialLinks()
        setupProfileCards()
    }

    private fun setupNavigation() {
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        val btnHome = findViewById<ImageView>(R.id.btn_home)
        btnHome.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun setupProfileCards() {
        val rudraCard = findViewById<MaterialCardView>(R.id.rudra_card)
        val prathamCard = findViewById<MaterialCardView>(R.id.pratham_card)

        // Add elevation animation when cards are pressed
        listOf(rudraCard, prathamCard).forEach { card ->
            card.setOnClickListener {
                animateCardClick(card)
            }
        }

        // Add shimmer effect to names
        val shimmer = AlphaAnimation(0.7f, 1.0f).apply {
            duration = 1500
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }

        rudraCard.findViewWithTag<TextView>("shimmer").startAnimation(shimmer)
        prathamCard.findViewWithTag<TextView>("shimmer").startAnimation(shimmer)
    }

    private fun setupSocialLinks() {
        // Social profile URLs
        val linkedinPratham = "https://www.linkedin.com/in/pratham-khanduja/"
        val githubPratham = "https://github.com/pratham-developer"
        val linkedinRudra = "https://www.linkedin.com/in/rudra-gupta-36827828b/"
        val githubRudra = "https://github.com/Rudragupta8777"

        // Pratham's buttons
        val btnLinkedinPratham = findViewById<MaterialCardView>(R.id.btn_linkedin_pratham)
        val btnGithubPratham = findViewById<MaterialCardView>(R.id.btn_github_pratham)

        // Rudra's buttons
        val btnLinkedinRudra = findViewById<MaterialCardView>(R.id.btn_linkedin_rudra)
        val btnGithubRudra = findViewById<MaterialCardView>(R.id.btn_github_rudra)

        // Apply ripple effects
        listOf(btnLinkedinPratham, btnGithubPratham, btnLinkedinRudra, btnGithubRudra).forEach {
            it.apply {
                isClickable = true
                isFocusable = true
                foreground = ContextCompat.getDrawable(context, R.drawable.button_ripple)
            }
        }

        // Set click listeners with animations
        btnLinkedinPratham.setOnClickListener {
            animateButtonClick(it)
            openUrl(linkedinPratham)
        }

        btnGithubPratham.setOnClickListener {
            animateButtonClick(it)
            openUrl(githubPratham)
        }

        btnLinkedinRudra.setOnClickListener {
            animateButtonClick(it)
            openUrl(linkedinRudra)
        }

        btnGithubRudra.setOnClickListener {
            animateButtonClick(it)
            openUrl(githubRudra)
        }
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        } catch (e: Exception) {
            // Handle exception if no browser available
        }
    }

    private fun animateButtonClick(view: View) {
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

    private fun animateCardClick(card: MaterialCardView) {
        card.animate()
            .scaleX(0.98f)
            .scaleY(0.98f)
            .setDuration(100)
            .withEndAction {
                card.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()

        // Elevation effect
        card.cardElevation = 16f
        card.postDelayed({ card.cardElevation = 8f }, 200)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}