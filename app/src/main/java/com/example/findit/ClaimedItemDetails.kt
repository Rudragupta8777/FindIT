package com.example.findit

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class ClaimedItemDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_claimed_item_details)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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
        val claimedByEditText = findViewById<EditText>(R.id.claimed_by_value)

        // Get data from intent
        val itemName = intent.getStringExtra("item_name") ?: "Item Name"
        val date = intent.getStringExtra("date") ?: ""
        val time = intent.getStringExtra("time") ?: ""
        val place = intent.getStringExtra("place") ?: ""
        val imageResource = intent.getStringExtra("image_resource")

        // Optional extras
        val contact = intent.getStringExtra("contact") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val reportedBy = intent.getStringExtra("reportedBy") ?: ""
        val claimedBy = intent.getStringExtra("claimedBy") ?: ""

        // Set data to views
        itemNameTextView.text = itemName


        if (!imageResource.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageResource)
                .centerCrop() // This will make the image fill the entire ImageView
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        // Keep image invisible if load fails
                        itemImageView.visibility = View.INVISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        // Make image visible when successfully loaded
                        itemImageView.visibility = View.VISIBLE
                        return false
                    }
                })
                .into(itemImageView)
        }


        dateFoundEditText.setText(date)
        timeFoundEditText.setText(time)
        locationEditText.setText(place)
        contactEditText.setText(contact)
        descriptionEditText.setText(description)
        reportedByEditText.setText(reportedBy)
        claimedByEditText.setText(claimedBy)

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