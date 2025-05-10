package com.example.findit

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my)

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

        val usersClaims = findViewById<MaterialButton>(R.id.my_claims)
        usersClaims.setOnClickListener {
            val intent = Intent(this, ItemClaimedHistory::class.java)
            startActivity(intent)
        }

        // Add click listener for My Reports button
        val usersReports = findViewById<MaterialButton>(R.id.my_reports)
        usersReports.setOnClickListener {
            val intent = Intent(this, ItemReportHistory::class.java)
            startActivity(intent)
        }
    }
}