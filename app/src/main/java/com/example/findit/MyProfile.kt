package com.example.findit

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

        // Get user data from intent
        firstName = intent.getStringExtra("firstName") ?: ""
        profilePicUrl = intent.getStringExtra("profilePicUrl") ?: ""
        val fullNameWithoutReg = intent.getStringExtra("fullNameWithoutReg") ?: ""

        // If not passed via intent, try to get from Firebase Auth
        if (firstName.isEmpty() || profilePicUrl.isEmpty()) {
            FirebaseAuth.getInstance().currentUser?.let { user ->
                if (firstName.isEmpty()) {
                    firstName = user.displayName?.split(" ")?.get(0) ?: ""
                }
                if (profilePicUrl.isEmpty()) {
                    profilePicUrl = user.photoUrl?.toString() ?: ""
                }
            }
        }

        // Display the name without registration number in the user_name TextView
        val userNameTextView = findViewById<TextView>(R.id.user_name)

        // Use the passed fullNameWithoutReg if available, otherwise extract it
        if (fullNameWithoutReg.isNotEmpty()) {
            userNameTextView.text = fullNameWithoutReg
        } else {
            val fullDisplayName = FirebaseAuth.getInstance().currentUser?.displayName ?: "User"
            // Extract name part (everything before the first digit)
            val nameOnly = fullDisplayName.split("\\d".toRegex())[0].trim()
            userNameTextView.text = nameOnly
        }

        // Load profile image
        val profileIconImageView = findViewById<ImageView>(R.id.profile_icon)
        if (profilePicUrl.isNotEmpty()) {
            Glide.with(this)
                .load(profilePicUrl)
                .circleCrop() // Makes the image circular
                .into(profileIconImageView)
        }

        // Display email
        val userEmailTextView = findViewById<TextView>(R.id.user_email)
        userEmailTextView.text = FirebaseAuth.getInstance().currentUser?.email ?: ""

        // Set up navigation and button click listeners
        val home = findViewById<ImageView>(R.id.btn_back)
        home.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }

        val yourActivity = findViewById<MaterialButton>(R.id.user_activity)
        yourActivity.setOnClickListener {
            val intent = Intent(this, MyActivity::class.java)
            startActivity(intent)
        }

        val termsAndCondition = findViewById<MaterialButton>(R.id.terms_and_conditions)
        termsAndCondition.setOnClickListener {
            val intent = Intent(this, TermsAndConditions::class.java)
            startActivity(intent)
        }

        val developersTeam = findViewById<MaterialButton>(R.id.developer_team)
        developersTeam.setOnClickListener {
            val intent = Intent(this, DevelopersTeam::class.java)
            startActivity(intent)
        }

        val signOut = findViewById<MaterialButton>(R.id.sign_out)
        signOut.setOnClickListener {
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut()

            // Return to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}