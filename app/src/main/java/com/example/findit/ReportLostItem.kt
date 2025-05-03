package com.example.findit

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReportLostItem : AppCompatActivity() {

    private lateinit var itemDateEditText: EditText
    private lateinit var itemTimeEditText: EditText
    private lateinit var photoImageView: ImageView
    private lateinit var cardDate: CardView
    private lateinit var cardTime: CardView
    private lateinit var cardPhoto: CardView

    private val calendar = Calendar.getInstance()

    // Activity result launcher for gallery selection
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                displaySelectedImage(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Activity result launcher for camera
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap
            displaySelectedImage(imageBitmap)
        }
    }

    // Helper method to display the image and adjust UI
    private fun displaySelectedImage(bitmap: Bitmap) {
        // Set the image and adjust display settings
        photoImageView.setImageBitmap(bitmap)
        photoImageView.scaleType = ImageView.ScaleType.CENTER_CROP

        // Hide the add image icon by adjusting padding
        photoImageView.setPadding(0, 0, 0, 0)

        // Remove the border drawable if it interferes
        cardPhoto.foreground = null
    }

    // Permission result launcher
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report_lost_item)

        // Initialize views
        itemDateEditText = findViewById(R.id.item_date)
        itemTimeEditText = findViewById(R.id.item_time)
        photoImageView = findViewById(R.id.item_photo)
        cardDate = findViewById(R.id.card_date)
        cardTime = findViewById(R.id.card_time)
        cardPhoto = findViewById(R.id.card_photo)
        val submit = findViewById<MaterialButton>(R.id.add_lost)
        val home = findViewById<ImageView>(R.id.btn_home)

        // Disable keyboard for date and time fields
        itemDateEditText.inputType = InputType.TYPE_NULL
        itemTimeEditText.inputType = InputType.TYPE_NULL

        // Set click listeners
        setupDatePicker()
        setupTimePicker()
        setupPhotoSelection()

        // Submit button handler (already implemented)
        submit.setOnClickListener {
            val intent = Intent(this, LostItemReportedSuccesfully::class.java)
            startActivity(intent)
        }

        // Home button handler (already implemented)
        home.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }
    }

    private fun setupDatePicker() {
        // Make both the CardView and EditText clickable for better UX
        val dateClickListener = {
            // Create DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    updateDateField()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        // Set the click listener to both the card and the edit text
        cardDate.setOnClickListener { dateClickListener() }
        itemDateEditText.setOnClickListener { dateClickListener() }
    }

    private fun setupTimePicker() {
        // Make both the CardView and EditText clickable for better UX
        val timeClickListener = {
            // Create TimePickerDialog
            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    updateTimeField()
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false // 24-hour format
            )
            timePickerDialog.show()
        }

        // Set the click listener to both the card and the edit text
        cardTime.setOnClickListener { timeClickListener() }
        itemTimeEditText.setOnClickListener { timeClickListener() }
    }

    private fun setupPhotoSelection() {
        // Make entire card photo clickable
        cardPhoto.setOnClickListener {
            showImagePickerDialog()
        }

        // Also keep the image view clickable for better UX
        photoImageView.setOnClickListener {
            showImagePickerDialog()
        }
    }

    private fun updateDateField() {
        val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        itemDateEditText.setText(dateFormat.format(calendar.time))
    }

    private fun updateTimeField() {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        itemTimeEditText.setText(timeFormat.format(calendar.time))
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> checkCameraPermissionAndOpen()
                1 -> openGallery()
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            cameraLauncher.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No camera application found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }
}