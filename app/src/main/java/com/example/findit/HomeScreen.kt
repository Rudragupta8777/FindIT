package com.example.findit

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.findit.data.User
import com.google.android.material.button.MaterialButton

class HomeScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)

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

        val nameText = findViewById<TextView>(R.id.user_name)
        val name = intent.getStringExtra("name")
        val firstName = name?.split(" ")?.firstOrNull() ?: ""
        nameText.setText(firstName+"!")




    }
}