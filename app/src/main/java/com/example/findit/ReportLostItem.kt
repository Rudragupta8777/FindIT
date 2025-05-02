package com.example.findit

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ReportLostItem : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report_lost_item)
        val submit = findViewById<MaterialButton>(R.id.add_lost)
        submit.setOnClickListener {
            val intent = Intent(this, LostItemReportedSuccesfully::class.java)
            startActivity(intent)
        }
        val home = findViewById<ImageView>(R.id.btn_home)
        home.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }
    }
}