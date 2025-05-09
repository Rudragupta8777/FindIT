package com.example.findit

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ClaimedItemDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_claimed_item_details)

        // Initialize views
        val backButton = findViewById<ImageView>(R.id.btn_back)
        val homeButton = findViewById<ImageView>(R.id.btn_home)
        val itemNameTextView = findViewById<TextView>(R.id.item_name)
        val itemImageView = findViewById<ImageView>(R.id.item_image)
        val dateFoundEditText = findViewById<EditText>(R.id.date_found_value)
        val timeFoundEditText = findViewById<EditText>(R.id.time_found_value)
        val locationEditText = findViewById<EditText>(R.id.location_value)
        val contactEditText = findViewById<EditText>(R.id.contact_value)
        val descriptionEditText = findViewById<EditText>(R.id.description_value)
        val reportedByEditText = findViewById<EditText>(R.id.reported_by_value)

        // Get data from intent
        val itemName = intent.getStringExtra("item_name") ?: "Item Name"
        val date = intent.getStringExtra("date") ?: ""
        val time = intent.getStringExtra("time") ?: ""
        val place = intent.getStringExtra("place") ?: ""
        val imageResource = intent.getIntExtra("image_resource", R.drawable.image_placeholder)

        // Optional extras
        val contact = intent.getStringExtra("contact") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val reportedBy = intent.getStringExtra("reported_by") ?: ""

        // Set data to views
        itemNameTextView.text = itemName
        itemImageView.setImageResource(imageResource)
        dateFoundEditText.setText(date)
        timeFoundEditText.setText(time)
        locationEditText.setText(place)
        contactEditText.setText(contact)
        descriptionEditText.setText(description)
        reportedByEditText.setText(reportedBy)

        // Set click listeners
        backButton.setOnClickListener {
            finish() // Go back to previous screen
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Clear activity stack
            startActivity(intent)
            finish()
        }
    }
}