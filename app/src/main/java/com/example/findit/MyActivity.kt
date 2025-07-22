package com.example.findit

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.navigation_bar_color)
            window.statusBarColor = ContextCompat.getColor(this, R.color.header)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

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