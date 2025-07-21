package com.example.findit

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import com.google.android.material.button.MaterialButton

class ItemDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item_details)
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
        val reporterregnoByEditText = findViewById<EditText>(R.id.reporter_regno_value)
        val claimButton = findViewById<Button>(R.id.btn_claim)

        // Get data from intent
        val itemName = intent.getStringExtra("item_name") ?: "Item Name"
        val date = intent.getStringExtra("date") ?: ""
        val time = intent.getStringExtra("time") ?: ""
        val place = intent.getStringExtra("place") ?: ""
        val imageResource = intent.getStringExtra("image_resource")
        val itemId = intent.getStringExtra("itemId")
        Log.d("Claim API", "item id = ${itemId}")

        // Optional extras - fixed key name to match adapter
        val contact = intent.getStringExtra("contact") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val reportedBy = intent.getStringExtra("reported_by") ?: "Unknown User"
        val reporter_reg_no = intent.getStringExtra("reporter_regno") ?: "No Reg.No"

        // Set data to views
        itemNameTextView.text = itemName.split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }

        // Load image with Glide
        if (!imageResource.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageResource)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
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
        reporterregnoByEditText.setText(reporter_reg_no)

        // Make contact field clickable but not editable
        contactEditText.isEnabled = true
        contactEditText.isFocusable = false
        contactEditText.isCursorVisible = false
        contactEditText.keyListener = null

        // Set up double-click listener for contact field
        setupContactActions(contactEditText)

        // Set click listeners
        backButton.setOnClickListener {
            finish()
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        claimButton.setOnClickListener {
            val intent = Intent(this, QRScannerActivity::class.java)
            intent.putExtra("itemId", itemId)
            startActivity(intent)
        }
    }

    private fun setupContactActions(contactEditText: EditText) {
        var clickCount = 0
        var lastClickTime = 0L
        val doubleClickThreshold = 500L // milliseconds
        val resetTime = 1000L // Reset click count after 1 second

        contactEditText.setOnClickListener {
            val currentTime = System.currentTimeMillis()

            // Reset click count if too much time has passed
            if (currentTime - lastClickTime > resetTime) {
                clickCount = 0
            }

            clickCount++
            lastClickTime = currentTime

            if (clickCount == 1) {
                // Double click detected
                clickCount = 0 // Reset counter
                val contactText = contactEditText.text.toString().trim()
                if (contactText.isNotEmpty()) {
                    showCustomContactDialog(contactText)
                }
            }
        }
    }

    private fun isPhoneNumber(text: String): Boolean {
        // Remove all non-digit characters for validation
        val digitsOnly = text.replace(Regex("[^0-9]"), "")

        // Check if it's exactly 10 digits (Indian mobile number)
        // You can modify this pattern based on your region's phone number format
        return digitsOnly.length == 10 && digitsOnly.matches(Regex("^[6-9]\\d{9}$"))
    }

    private fun showCustomContactDialog(contactText: String) {
        val isPhone = isPhoneNumber(contactText)

        // Inflate the custom dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_copy, null)

        // Get references to dialog views
        val copyButton = dialogView.findViewById<MaterialButton>(R.id.btn_copy)
        val callButton = dialogView.findViewById<MaterialButton>(R.id.btn_reports)

        // Show or hide call button based on phone number validation
        if (isPhone) {
            callButton.visibility = View.VISIBLE
        } else {
            callButton.visibility = View.GONE
        }

        // Create the dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Set button click listeners
        copyButton.setOnClickListener {
            copyToClipboard(contactText)
            dialog.dismiss()
        }

        callButton.setOnClickListener {
            makePhoneCall(contactText)
            dialog.dismiss()
        }

        // Make dialog background transparent to show custom background
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Contact", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Contact copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun makePhoneCall(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to make call", Toast.LENGTH_SHORT).show()
            Log.e("ItemDetails", "Error making phone call", e)
        }
    }
}