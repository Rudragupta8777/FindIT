package com.example.findit

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
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

    private lateinit var itemNameEditText: EditText
    private lateinit var itemDateEditText: EditText
    private lateinit var itemTimeEditText: EditText
    private lateinit var itemPlaceEditText: EditText
    private lateinit var itemDescriptionEditText: EditText
    private lateinit var itemContactEditText: EditText
    private lateinit var photoImageView: ImageView
    private lateinit var cardDate: CardView
    private lateinit var cardTime: CardView
    private lateinit var cardPhoto: CardView
    private lateinit var cardDescription: CardView
    private lateinit var cardItemType: CardView
    private lateinit var itemTypeSpinner: Spinner

    // Error TextViews
    private lateinit var errorItemName: TextView
    private lateinit var errorDate: TextView
    private lateinit var errorTime: TextView
    private lateinit var errorPlace: TextView
    private lateinit var errorDescription: TextView
    private lateinit var errorContact: TextView
    private lateinit var errorPhoto: TextView
    private lateinit var errorItemType: TextView

    private val calendar = Calendar.getInstance()
    private var photoSelected = false
    private var itemTypeSelected = false

    // Activity result launcher for gallery selection
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                displaySelectedImage(bitmap)
                photoSelected = true
                hideError(errorPhoto)
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
            photoSelected = true
            hideError(errorPhoto)
        }
    }

    // Helper method to display the image and adjust UI
    private fun displaySelectedImage(bitmap: Bitmap) {
        // Set the image and adjust display settings
        photoImageView.setImageBitmap(bitmap)
        photoImageView.scaleType = ImageView.ScaleType.CENTER_CROP

        // Make the image fill the entire card and remove any padding
        photoImageView.setPadding(0, 0, 0, 0)

        // Set the background of the card to be transparent
        cardPhoto.setCardBackgroundColor(android.graphics.Color.TRANSPARENT)

        // Keep only the border by maintaining the foreground drawable
        cardPhoto.foreground = ContextCompat.getDrawable(this, R.drawable.photo_input_box)
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
        itemNameEditText = findViewById(R.id.item_name)
        itemDateEditText = findViewById(R.id.item_date)
        itemTimeEditText = findViewById(R.id.item_time)
        itemPlaceEditText = findViewById(R.id.item_place)
        itemDescriptionEditText = findViewById(R.id.item_description)
        itemContactEditText = findViewById(R.id.item_contact)
        photoImageView = findViewById(R.id.item_photo)

        cardDate = findViewById(R.id.card_date)
        cardTime = findViewById(R.id.card_time)
        cardPhoto = findViewById(R.id.card_photo)
        cardDescription = findViewById(R.id.card_description)
        cardItemType = findViewById(R.id.card_item_type)

        val submit = findViewById<MaterialButton>(R.id.add_lost)
        val home = findViewById<ImageView>(R.id.btn_home)
        itemTypeSpinner = findViewById(R.id.item_type_spinner)

        // Setup Spinner
        val itemTypes = listOf("Select Item Type", "Electronics", "ID Card", "Clothing", "Books", "Accessories", "Others")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, itemTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        itemTypeSpinner.adapter = adapter

        // Add spinner item selection listener
        itemTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // If user selects anything other than "Select Item Type" (position 0), consider it selected
                itemTypeSelected = position != 0
                if (itemTypeSelected) {
                    hideError(errorItemType)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                itemTypeSelected = false
            }
        }

        // Initialize error TextViews
        createErrorTextViews()

        // Disable keyboard for date and time fields
        itemDateEditText.inputType = InputType.TYPE_NULL
        itemTimeEditText.inputType = InputType.TYPE_NULL

        // Set click listeners
        setupDatePicker()
        setupTimePicker()
        setupPhotoSelection()
        setupDescriptionCard()

        // Setup text change listeners to hide errors when user types
        setupTextChangeListeners()

        // Submit button handler with validation
        submit.setOnClickListener {
            if (validateForm()) {
                val intent = Intent(this, LostItemReportedSuccesfully::class.java)
                startActivity(intent)
            }
        }

        // Home button handler
        home.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }
    }


    private fun setupDescriptionCard() {
        // Make the entire description card clickable and focus on the EditText
        cardDescription.setOnClickListener {
            itemDescriptionEditText.requestFocus()
        }
    }

    private fun createErrorTextViews() {
        // Create error TextViews after each field
        errorItemName = createErrorTextView(R.id.card_item_name)
        errorItemType = createErrorTextView(R.id.card_item_type)
        errorDate = createErrorTextView(R.id.card_date)
        errorTime = createErrorTextView(R.id.card_time)
        errorPlace = createErrorTextView(R.id.card_place)
        errorDescription = createErrorTextView(R.id.card_description)
        errorPhoto = createErrorTextView(R.id.card_photo)
        errorContact = createErrorTextView(R.id.card_contact)
    }

    private fun createErrorTextView(belowViewId: Int): TextView {
        val errorTextView = TextView(this)
        val belowView = findViewById<View>(belowViewId)
        val parentView = belowView.parent as androidx.constraintlayout.widget.ConstraintLayout

        errorTextView.id = View.generateViewId()
        errorTextView.text = "This field is required"
        errorTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
        errorTextView.visibility = View.GONE
        errorTextView.textSize = 12f

        val params = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
            androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT,
            androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        params.topToBottom = belowViewId
        params.startToStart = belowViewId
        params.topMargin = 4
        params.marginStart = 8

        parentView.addView(errorTextView, params)

        return errorTextView
    }

    private fun setupTextChangeListeners() {
        itemNameEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                hideError(errorItemName)
            }
        })

        itemDateEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                hideError(errorDate)
            }
        })

        itemTimeEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                hideError(errorTime)
            }
        })

        itemPlaceEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                hideError(errorPlace)
            }
        })

        itemDescriptionEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                hideError(errorDescription)
            }
        })

        itemContactEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                hideError(errorContact)
            }
        })
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Check item name
        if (itemNameEditText.text.toString().trim().isEmpty()) {
            showError(errorItemName, findViewById(R.id.card_item_name))
            isValid = false
        }

        // Check item type
        if (!itemTypeSelected) {
            showError(errorItemType, cardItemType)
            isValid = false
        }

        // Check date
        if (itemDateEditText.text.toString().trim().isEmpty()) {
            showError(errorDate, cardDate)
            isValid = false
        }

        // Check time
        if (itemTimeEditText.text.toString().trim().isEmpty()) {
            showError(errorTime, cardTime)
            isValid = false
        }

        // Check place
        if (itemPlaceEditText.text.toString().trim().isEmpty()) {
            showError(errorPlace, findViewById(R.id.card_place))
            isValid = false
        }

        // Check description
        if (itemDescriptionEditText.text.toString().trim().isEmpty()) {
            showError(errorDescription, cardDescription)
            isValid = false
        }

        // Check photo
        if (!photoSelected) {
            showError(errorPhoto, cardPhoto)
            isValid = false
        }

        // Check contact
        if (itemContactEditText.text.toString().trim().isEmpty()) {
            showError(errorContact, findViewById(R.id.card_contact))
            isValid = false
        }

        return isValid
    }

    private fun showError(errorTextView: TextView, viewToShake: View) {
        // Show error message
        errorTextView.visibility = View.VISIBLE

        // Shake animation
        val shakeAnim = AnimatorSet()
        val animDuration = 100L

        val shake1 = ObjectAnimator.ofFloat(viewToShake, "translationX", 0f, 15f)
        shake1.duration = animDuration

        val shake2 = ObjectAnimator.ofFloat(viewToShake, "translationX", 15f, -15f)
        shake2.duration = animDuration

        val shake3 = ObjectAnimator.ofFloat(viewToShake, "translationX", -15f, 15f)
        shake3.duration = animDuration

        val shake4 = ObjectAnimator.ofFloat(viewToShake, "translationX", 15f, -15f)
        shake4.duration = animDuration

        val shake5 = ObjectAnimator.ofFloat(viewToShake, "translationX", -15f, 0f)
        shake5.duration = animDuration

        shakeAnim.playSequentially(shake1, shake2, shake3, shake4, shake5)
        shakeAnim.interpolator = AccelerateDecelerateInterpolator()
        shakeAnim.start()

        // Change border color to red
        viewToShake.foreground = ContextCompat.getDrawable(this, R.drawable.input_box_error)
    }

    private fun hideError(errorTextView: TextView) {
        errorTextView.visibility = View.GONE

        // Reset border colors
        when (errorTextView) {
            errorItemName -> findViewById<CardView>(R.id.card_item_name).foreground =
                ContextCompat.getDrawable(this, R.drawable.input_box)
            errorItemType -> cardItemType.foreground =
                ContextCompat.getDrawable(this, R.drawable.input_box)
            errorDate -> cardDate.foreground =
                ContextCompat.getDrawable(this, R.drawable.input_box)
            errorTime -> cardTime.foreground =
                ContextCompat.getDrawable(this, R.drawable.input_box)
            errorPlace -> findViewById<CardView>(R.id.card_place).foreground =
                ContextCompat.getDrawable(this, R.drawable.input_box)
            errorDescription -> cardDescription.foreground =
                ContextCompat.getDrawable(this, R.drawable.input_box)
            errorPhoto -> cardPhoto.foreground =
                ContextCompat.getDrawable(this, R.drawable.photo_input_box)
            errorContact -> findViewById<CardView>(R.id.card_contact).foreground =
                ContextCompat.getDrawable(this, R.drawable.input_box)
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
                    hideError(errorDate)
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
                    hideError(errorTime)
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