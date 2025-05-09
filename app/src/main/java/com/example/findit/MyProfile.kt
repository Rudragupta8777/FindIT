package com.example.findit

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class MyProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_profile)

        val home = findViewById<ImageView>(R.id.btn_back)
        home.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }

        val YourActivity = findViewById<MaterialButton>(R.id.user_activity)
        YourActivity.setOnClickListener {
            val intent = Intent(this, MyActivity::class.java)
            startActivity(intent)
        }

        val terms_and_condition = findViewById<MaterialButton>(R.id.terms_and_conditions)
        terms_and_condition.setOnClickListener {
            val intent = Intent(this, TermsAndConditions::class.java)
            startActivity(intent)
        }

        val developersTeam = findViewById<MaterialButton>(R.id.developer_team)
        developersTeam.setOnClickListener {
            val intent = Intent(this, DevelopersTeam::class.java)
            startActivity(intent)
        }
    }
}