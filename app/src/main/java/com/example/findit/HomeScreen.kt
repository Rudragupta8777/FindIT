package com.example.findit

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class HomeScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_screen)

        val findLostItem = findViewById<MaterialButton>(R.id.btn_find_lost)
        findLostItem.setOnClickListener {
            val intent = Intent(this, FindLostItem::class.java)
            startActivity(intent)
        }

    }
}