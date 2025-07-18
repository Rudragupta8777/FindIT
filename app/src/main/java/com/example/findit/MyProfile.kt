package com.example.findit

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.text.util.Linkify
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class MyProfile : AppCompatActivity() {

    private var firstName: String = ""
    private var profilePicUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_profile)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Get user data from intent
        firstName = intent.getStringExtra("firstName") ?: ""
        profilePicUrl = intent.getStringExtra("profilePicUrl") ?: ""
        val fullNameWithoutReg = intent.getStringExtra("fullNameWithoutReg") ?: ""

        // Load from SharedPreferences if not passed via intent
        if (firstName.isEmpty()) {
            firstName = sharedPref.getString("userName", "") ?: ""
        }

        if (profilePicUrl.isEmpty()) {
            profilePicUrl = sharedPref.getString("profilePicUrl", "") ?: ""
        }

        if (firstName.isEmpty() || profilePicUrl.isEmpty()) {
            FirebaseAuth.getInstance().currentUser?.let { user ->
                if (firstName.isEmpty()) {
                    firstName = user.displayName?.split(" ")?.get(0) ?: "User"
                }
                if (profilePicUrl.isEmpty()) {
                    profilePicUrl = user.photoUrl?.toString() ?: ""
                }
            }
        }

        // Display name
        val userNameTextView = findViewById<TextView>(R.id.user_name)
        if (fullNameWithoutReg.isNotEmpty()) {
            userNameTextView.text = fullNameWithoutReg
        } else {
            val fullDisplayName = FirebaseAuth.getInstance().currentUser?.displayName ?: "User"
            val nameOnly = fullDisplayName.split("\\d".toRegex())[0].trim()
            userNameTextView.text = nameOnly
        }

        // Load profile image
        val profileIconImageView = findViewById<ImageView>(R.id.profile_icon)
        if (profilePicUrl.isNotEmpty()) {
            Glide.with(this)
                .load(profilePicUrl)
                .circleCrop()
                .into(profileIconImageView)
        }

        // Display email
        val userEmailTextView = findViewById<TextView>(R.id.user_email)
        userEmailTextView.text = FirebaseAuth.getInstance().currentUser?.email ?: ""

        // Navigation buttons
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            startActivity(Intent(this, HomeScreen::class.java))
        }

        findViewById<MaterialButton>(R.id.user_activity).setOnClickListener {
            showActivityPopup()
        }

        findViewById<MaterialButton>(R.id.contact_us).setOnClickListener {
            showContactUsPopup()
        }

        findViewById<MaterialButton>(R.id.terms_and_conditions).setOnClickListener {
            startActivity(Intent(this, TermsAndConditions::class.java))
        }

        findViewById<MaterialButton>(R.id.developer_team).setOnClickListener {
            startActivity(Intent(this, DevelopersTeam::class.java))
        }

        // Sign Out
        findViewById<MaterialButton>(R.id.sign_out).setOnClickListener {
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut()

            // Clear stored SharedPreferences
            sharedPref.edit().clear().apply()

            // Go back to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun showActivityPopup() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_my_activity, null)
        val builder = AlertDialog.Builder(this).apply {
            setView(dialogView)
            setCancelable(true)
        }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnClaims = dialogView.findViewById<MaterialButton>(R.id.btn_claims)
        val btnReports = dialogView.findViewById<MaterialButton>(R.id.btn_reports)

        btnClaims.setOnClickListener {
            startActivity(Intent(this, ItemClaimedHistory::class.java))
            dialog.dismiss()
        }

        btnReports.setOnClickListener {
            startActivity(Intent(this, ItemReportHistory::class.java))
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showContactUsPopup() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_contact_us, null)
        val builder = AlertDialog.Builder(this).apply {
            setView(dialogView)
            setCancelable(true)
        }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val emailText = dialogView.findViewById<TextView>(R.id.email_link)
        Linkify.addLinks(emailText, Linkify.EMAIL_ADDRESSES)
        emailText.setLinkTextColor(ContextCompat.getColor(this, android.R.color.white))

        emailText.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@finditapp.me")
            }
            startActivity(Intent.createChooser(intent, "Send Email"))
            dialog.dismiss()
        }

        dialog.show()
    }
}