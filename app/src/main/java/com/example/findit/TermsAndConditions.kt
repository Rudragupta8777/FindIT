package com.example.findit

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat

class TermsAndConditions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_conditions)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.navigation_bar_color)
            window.statusBarColor = ContextCompat.getColor(this, R.color.header)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        // Find TextView for terms text
        val termsTextView = findViewById<TextView>(R.id.terms_text)

        // Set the formatted text with bold section titles
        val termsText = """
            <b>1. Introduction</b><br>
            Welcome to <b>FindIt</b>, a community platform developed to assist students and staff in locating lost items and reporting found items. By using this app, you agree to the terms outlined below.<br><br>
        
            <b>2. Acceptance of Terms</b><br>
            By accessing or using FindIt, you agree to be bound by these Terms and Conditions. If you do not accept these terms, please refrain from using the app.<br><br>
        
            <b>3. User Responsibilities</b><br>
            &#8226; <b>Accurate Information:</b> Ensure that all details provided about found items are truthful and complete.<br>
            &#8226; <b>Prohibited Listings:</b> Do not post items that are illegal, unsafe, or against institutional policies.<br>
            &#8226; <b>Respectful Conduct:</b> Maintain civility and professionalism when interacting with other users. Harassment and inappropriate behavior will not be tolerated.<br><br>
        
            <b>4. Lost and Found Listings</b><br>
            &#8226; <b>Found Items:</b> Accurately detail the item and the location and date it was discovered.<br>
            &#8226; <b>Ownership Verification:</b> The claimant may be asked to provide proof of ownership to confirm the rightful owner.<br><br>
        
            <b>5. App Usage</b><br>
            &#8226; <b>Non-Commercial Use:</b> The app is for personal use only within the campus community. Commercial usage is strictly prohibited.<br>
            &#8226; <b>No Misuse:</b> Posting false or misleading information is prohibited.<br>
            &#8226; <b>Account Responsibility:</b> You are responsible for maintaining the security of your account credentials.<br><br>
        
            <b>6. Privacy</b><br>
            We prioritize your privacy. Please review the Privacy for information on how we collect, use, and protect your data.<br><br>
        
            <b>7. Limitation of Liability</b><br>
            FindIt is provided "as is". We do not guarantee the recovery of lost items or the return of found items. We are not liable for any loss or damage resulting from the use of this app.<br><br>
        
            <b>8. Changes to Terms</b><br>
            We may modify these Terms and Conditions at any time. Continued use of the app constitutes acceptance of any updated terms.<br><br>
        
            <b>9. Termination</b><br>
            We reserve the right to suspend or terminate your access to FindIt if any terms are violated or inappropriate activity is identified.<br><br>
        
            <b>10. Governing Law</b><br>
            These Terms are governed by the laws applicable in the jurisdiction of the affiliated institution. Disputes will be handled accordingly.
        """.trimIndent()

        termsTextView.text = Html.fromHtml(termsText, Html.FROM_HTML_MODE_COMPACT)



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
}