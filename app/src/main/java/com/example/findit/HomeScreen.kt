package com.example.findit

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class HomeScreen : AppCompatActivity() {

    private var firstName: String = ""
    private var profilePicUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Get user data from intent
        firstName = intent.getStringExtra("firstName") ?: ""
        profilePicUrl = intent.getStringExtra("profilePicUrl") ?: ""

        // If not passed via intent, try to get from Firebase Auth
        if (firstName.isEmpty()) {
            FirebaseAuth.getInstance().currentUser?.let { user ->
                // Get only the first word as the first name
                firstName = user.displayName?.split(" ")?.get(0) ?: ""
            }
        }

        // Display the first name + "!" in the userName TextView
        val userNameTextView = findViewById<TextView>(R.id.user_name)
        userNameTextView.text = "$firstName!"

        // We're keeping the default profile icon in HomeScreen as requested
        // Not loading the user profile picture here
        val profileImageView = findViewById<ImageView>(R.id.profile)

        // Set up navigation and button click listeners
        profileImageView.setOnClickListener {
            val intent = Intent(this, MyProfile::class.java).apply {
                putExtra("firstName", firstName)
                putExtra("profilePicUrl", profilePicUrl)
                putExtra("fullNameWithoutReg", intent.getStringExtra("fullNameWithoutReg") ?: "")
            }
            startActivity(intent)
        }

        val findLostItem = findViewById<MaterialButton>(R.id.btn_find_lost)
        findLostItem.setOnClickListener {
            val intent = Intent(this, FindItem::class.java)
            startActivity(intent)
        }

        val reportLostItem = findViewById<MaterialButton>(R.id.btn_report_lost)
        reportLostItem.setOnClickListener {
            val intent = Intent(this, ReportLostItem::class.java)
            startActivity(intent)
        }
    }
}