package com.example.findit

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class TermsAndConditions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_terms_and_conditions)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Find TextView for terms text
        val termsTextView = findViewById<TextView>(R.id.terms_text)

        // Set the formatted text with bold section titles
        val termsText = "<b>1. Introduction</b><br>" +
                "- Welcome to \"FindIt,\" a college-based community app developed to help students and staff find their lost items and list items they have found. By using our app, you agree to abide by the following terms and conditions. Please read them carefully.<br><br>" +

                "<b>2. Acceptance of Terms</b><br>" +
                "- By accessing and using \"FindIt,\" you accept and agree to be bound by these terms and conditions. If you do not agree with any part of these terms, you should not use the app.<br><br>" +

                "<b>3. User Responsibilities</b><br>" +
                "- Accuracy of Information: Users must provide accurate and truthful information when listing lost or found items.<br>" +
                "- Prohibited Items: Do not list any items that are illegal, dangerous, or prohibited by college policies.<br>" +
                "- Respect for Others: Treat all users with respect and courtesy. Harassment, abusive language, or inappropriate behaviour will not be tolerated.<br><br>" +

                "<b>4. Listing Lost and Found Items</b><br>" +
                "- Lost Items: When listing a lost item, provide a clear and detailed description, including the date and location where it was last seen.<br>" +
                "- Found Items: When listing a found item, provide a clear and detailed description, including the date and location where it was found.<br>" +
                "- Ownership Verification: The person claiming a lost item may be required to provide proof of ownership to ensure the rightful owner is reunited with their property.<br><br>" +

                "<b>5. App Usage</b><br>" +
                "- No Commercial Use: The app is intended for personal use within the college community. Commercial use of the app is prohibited.<br>" +
                "- No Misuse: Do not use the app for any fraudulent activities or to list false information.<br>" +
                "- Account Security: You are responsible for maintaining the confidentiality of your account information and for all activities that occur under your account.<br><br>" +

                "<b>6. Privacy Policy</b><br>" +
                "- We value your privacy. Please review our Privacy Policy to understand how we collect, use, and protect your personal information.<br><br>" +

                "<b>7. Limitation of Liability</b><br>" +
                "- \"FindIt\" is provided on an \"as-is\" basis. We do not guarantee that all lost items will be found or that all found items will be returned to their rightful owners. We are not responsible for any direct, indirect, incidental, or consequential damages arising from the use of the app.<br><br>" +

                "<b>8. Changes to Terms</b><br>" +
                "- We reserve the right to modify these terms and conditions at any time. Any changes will be posted on the app, and your continued use of the app after such changes constitutes your acceptance of the new terms.<br><br>" +

                "<b>9. Termination</b><br>" +
                "- We reserve the right to terminate your access to \"FindIt\" at our discretion, without notice, if you violate these terms and conditions or engage in any conduct that we deem inappropriate.<br><br>" +

                "<b>10. Governing Law</b><br>" +
                "- These terms and conditions are governed by the laws of the jurisdiction in which the college is located. Any disputes arising from the use of the app will be resolved in accordance with these laws."

        // Set the HTML formatted text
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