package com.example.findit

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.graphics.Matrix
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.text.InputType
import android.util.DisplayMetrics
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.findit.objects.RetrofitInstance
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone.*
import java.util.UUID

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
    private lateinit var submitButton: MaterialButton

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
    private var selectedImageBitmap: Bitmap? = null
    private var isSubmitting = false

    private lateinit var loaderOverlay: FrameLayout
    private lateinit var vibrator: Vibrator

    // Activity result launcher for gallery selection
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                displaySelectedImage(bitmap)
                photoSelected = true
                selectedImageBitmap = bitmap
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
            selectedImageBitmap = imageBitmap
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

    private fun vibratePhone() {
        if (vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(200)
            }
        }
    }

    inner class CustomSpinnerAdapter(
        context: Context,
        resource: Int,
        private val items: List<String>
    ) : ArrayAdapter<String>(context, resource, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            val textView = view as TextView

            // Set white text color for the selected item shown in spinner
            textView.setTextColor(Color.WHITE)
            textView.textSize = 18f

            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            val textView = view as TextView

            // Set white text and dark background for dropdown items
            textView.setTextColor(Color.WHITE)
            textView.setBackgroundColor(Color.parseColor("#2E2D2D"))
            textView.setPadding(20, 18, 20, 18) // Increased padding for better readability
            textView.textSize = 18f

            // Add separator line at bottom of each item (except last one)
            if (position < items.size - 1) {
                val separatorColor = Color.parseColor("#4A4A4A")
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
                    createSeparatorDrawable(separatorColor))
                textView.compoundDrawablePadding = 18
            } else {
                // Remove separator for last item
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            return view
        }

        // Create a separator line drawable
        private fun createSeparatorDrawable(color: Int): android.graphics.drawable.Drawable {
            val drawable = android.graphics.drawable.GradientDrawable()
            drawable.shape = android.graphics.drawable.GradientDrawable.RECTANGLE
            drawable.setColor(color)
            drawable.setSize(-1, 2) // Full width, 2px height
            return drawable
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_lost_item)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        loaderOverlay = findViewById(R.id.loader_overlay)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.navigation_bar_color)
            window.statusBarColor = ContextCompat.getColor(this, R.color.header)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

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

        submitButton = findViewById(R.id.add_lost)
        val home = findViewById<ImageView>(R.id.btn_home)
        itemTypeSpinner = findViewById(R.id.item_type_spinner)

        // Initialize vibrator
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Setup Spinner
        val itemTypes = listOf("Select Item Type", "Electronics", "ID Card", "Clothing", "Books", "Accessories", "Others")
        val adapter = CustomSpinnerAdapter(this, R.layout.spinner_item, itemTypes)
        itemTypeSpinner.adapter = adapter

        // Style the spinner popup with rounded corners
        itemTypeSpinner.post {
            try {
                val popup = Spinner::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true
                val listPopupWindow = popup.get(itemTypeSpinner) as? android.widget.ListPopupWindow

                listPopupWindow?.let { popupWindow ->
                    // Create rounded background drawable
                    val backgroundDrawable = android.graphics.drawable.GradientDrawable().apply {
                        setColor(Color.parseColor("#2E2D2D"))
                        cornerRadius = 12f // Curved edges (12dp radius)
                        setStroke(2, Color.parseColor("#6FFFEEC3")) // Gold border
                    }
                    popupWindow.setBackgroundDrawable(backgroundDrawable)

                    // Add some margin/padding for better appearance
                    popupWindow.horizontalOffset = 0
                    popupWindow.verticalOffset = 4
                }
            } catch (e: Exception) {
                // Fallback: if reflection fails, spinner will still work without rounded corners
                e.printStackTrace()
            }
        }

        // Add spinner item selection listener
        itemTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Force text color to white for selected item (additional safety)
                (view as? TextView)?.setTextColor(Color.WHITE)

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

        // Force white text color after spinner setup
        itemTypeSpinner.post {
            val spinnerText = itemTypeSpinner.selectedView as? TextView
            spinnerText?.setTextColor(Color.WHITE)
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
        submitButton.setOnClickListener {
            if (!isSubmitting && validateForm()) {
                submitReport()
            }
        }

        // Home button handler
        home.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }
    }

    // Update the submitReport function to properly handle dateLost:

    private fun submitReport() {
        isSubmitting = true
        submitButton.isEnabled = false
        submitButton.text = "Submitting..."

        // Compression happens here:
        val compressedBitmap = compressImage(selectedImageBitmap!!)
        val imageFile = convertBitmapToFile(compressedBitmap)

        // Verify final size
        val finalSizeMB = imageFile.length().toDouble() / (1024 * 1024)
        Log.d("FinalSize", "Compressed to: %.2f MB".format(finalSizeMB))

        // Create multipart request
        val imageRequestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)

        // Create request bodies for text fields
        val title = itemNameEditText.text.toString().trim()
            .toRequestBody("text/plain".toMediaTypeOrNull())

        val description = itemDescriptionEditText.text.toString().trim()
            .toRequestBody("text/plain".toMediaTypeOrNull())

        val contact = itemContactEditText.text.toString().trim()
            .toRequestBody("text/plain".toMediaTypeOrNull())

        val category = itemTypeSpinner.selectedItem.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())

        val location = itemPlaceEditText.text.toString().trim()
            .toRequestBody("text/plain".toMediaTypeOrNull())

        // Extract date and time from their respective fields
        val dateStr = itemDateEditText.text.toString()
        val timeStr = itemTimeEditText.text.toString()

        // Create a new Calendar instance to avoid modifying the current one
        val lostCalendar = Calendar.getInstance()

        // Parse the date
        try {
            val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            val parsedDate = dateFormat.parse(dateStr)
            if (parsedDate != null) {
                lostCalendar.time = parsedDate
            }

            // Parse the time
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val parsedTime = timeFormat.parse(timeStr)
            if (parsedTime != null) {
                // Extract hour, minute, second from parsed time
                val timeCalendar = Calendar.getInstance()
                timeCalendar.time = parsedTime

                // Set the time components on the lostCalendar
                lostCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                lostCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
                lostCalendar.set(Calendar.SECOND, 0)
                lostCalendar.set(Calendar.MILLISECOND, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error parsing date/time: ${e.message}", Toast.LENGTH_SHORT).show()
            isSubmitting = false
            submitButton.isEnabled = true
            submitButton.text = "Submit"
            return
        }

        // Format to ISO 8601 format for API
        val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
// Set the formatter timezone to UTC
        apiDateFormat.timeZone = getTimeZone("UTC")

        val dateLost = apiDateFormat.format(lostCalendar.time)
            .toRequestBody("text/plain".toMediaTypeOrNull())


        // Use coroutine to make the API call
        loaderOverlay.visibility = View.VISIBLE // Show loader

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.authItemApi.createItem(
                    title = title,
                    description = description,
                    contact = contact,
                    category = category,
                    location = location,
                    dateFound = dateLost,
                    image = imagePart
                )

                runOnUiThread {
                    isSubmitting = false
                    submitButton.isEnabled = true
                    submitButton.text = "Submit"

                    if (response.isSuccessful) {
                        // Navigate to success screen
                        val intent = Intent(this@ReportLostItem, LostItemReportedSuccesfully::class.java)
                        startActivity(intent)
                        finish()  // Optional: Close this activity
                    } else {
                        // Show error message
                        Toast.makeText(
                            this@ReportLostItem,
                            "Error: ${response.errorBody()?.string() ?: "Unknown error"}",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("Report item", "${response.errorBody()?.string()} : ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    isSubmitting = false
                    submitButton.isEnabled = true
                    submitButton.text = "Submit"
                    Toast.makeText(
                        this@ReportLostItem,
                        "Network error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("Report item", "Exception: ${e.message}", e)
                }
            } finally {
                loaderOverlay.visibility = View.GONE // Hide loader after data load
            }
        }
    }

    private fun compressImage(originalBitmap: Bitmap): Bitmap {
        // Step 1: Determine if this is a very large image
        val isVeryLargeFile = originalBitmap.byteCount > 8_000_000 // ~8MB in memory

        // Step 2: Scale down based on original size
        var scaledBitmap = originalBitmap
        val scaleFactor = when {
            originalBitmap.byteCount > 15_000_000 -> 0.25f  // Extreme compression for >15MB
            originalBitmap.byteCount > 8_000_000 -> 0.4f    // Heavy compression for 8-15MB
            originalBitmap.byteCount > 3_000_000 -> 0.6f    // Moderate compression for 3-8MB
            else -> 1f                                      // No scaling for small images
        }

        if (scaleFactor < 1f) {
            scaledBitmap = scaleBitmap(originalBitmap, scaleFactor)
        }

        // Step 3: Compress with quality reduction
        var quality = if (isVeryLargeFile) 80 else 90 // Start with lower quality for large files
        val outputStream = ByteArrayOutputStream()
        var lastGoodBitmap: Bitmap? = null

        do {
            outputStream.reset()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

            // Store the last acceptable compression
            if (outputStream.size() <= if (isVeryLargeFile) 3_000_000 else 1_000_000) {
                lastGoodBitmap = BitmapFactory.decodeByteArray(
                    outputStream.toByteArray(), 0, outputStream.size()
                )
            }

            quality -= if (isVeryLargeFile) 15 else 10 // More aggressive reduction for large files

            // Early exit if we've gone too low
            if (quality < if (isVeryLargeFile) 30 else 40) break
        } while (outputStream.size() > if (isVeryLargeFile) 3_000_000 else 1_000_000)

        return lastGoodBitmap ?: scaledBitmap.also {
            Log.w("ImageCompression", "Using fallback scaled bitmap")
        }
    }

    private fun scaleBitmap(bitmap: Bitmap, scaleFactor: Float): Bitmap {
        val matrix = Matrix().apply {
            postScale(scaleFactor, scaleFactor)
            // Optional: Add slight sharpening for scaled-down images
            postConcat(SharpeningMatrix(0.2f))
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    // Helper for slight sharpening after scaling
    private class SharpeningMatrix(strength: Float) : Matrix() {
        init {
            val values = floatArrayOf(
                1f, 0f, 0f,
                0f, 1f, 0f,
                0f, 0f, 1f
            )
            values[0] = 1 + strength
            values[4] = 1 + strength
            values[8] = 1 - strength * 2
            setValues(values)
        }
    }

    private fun convertBitmapToFile(bitmap: Bitmap): File {
        // Create a file in the cache directory
        val file = File(cacheDir, "image_${UUID.randomUUID()}.jpg")
        file.createNewFile()

        // Convert bitmap to file
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream.flush()
        outputStream.close()

        return file
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

        vibratePhone()

        // Change border color to red
        viewToShake.foreground = ContextCompat.getDrawable(this, R.drawable.input_box_error)
    }

    private fun hideError(errorTextView: TextView) {
        errorTextView.visibility = View.GONE

        // Reset border colors
        when (errorTextView) {
            errorItemName -> findViewById<CardView>(R.id.card_item_name).foreground =
                ContextCompat.getDrawable(this, R.drawable.kt_input_box)
            errorItemType -> cardItemType.foreground =
                ContextCompat.getDrawable(this, R.drawable.kt_input_box)
            errorDate -> cardDate.foreground =
                ContextCompat.getDrawable(this, R.drawable.kt_input_box)
            errorTime -> cardTime.foreground =
                ContextCompat.getDrawable(this, R.drawable.kt_input_box)
            errorPlace -> findViewById<CardView>(R.id.card_place).foreground =
                ContextCompat.getDrawable(this, R.drawable.kt_input_box)
            errorDescription -> cardDescription.foreground =
                ContextCompat.getDrawable(this, R.drawable.kt_input_box)
            errorPhoto -> cardPhoto.foreground =
                ContextCompat.getDrawable(this, R.drawable.photo_input_box)
            errorContact -> findViewById<CardView>(R.id.card_contact).foreground =
                ContextCompat.getDrawable(this, R.drawable.kt_input_box)
        }
    }

    private fun setupDatePicker() {
        itemDateEditText.apply {
            inputType = InputType.TYPE_NULL
            showSoftInputOnFocus = false
            isFocusable = false
            isFocusableInTouchMode = false
            keyListener = null
        }

        val dateClickListener = {
            val contextThemeWrapper = ContextThemeWrapper(this, android.R.style.Theme_Holo_Dialog) // default dark theme

            val datePickerDialog = DatePickerDialog(
                contextThemeWrapper,
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
            ).apply {
                datePicker.maxDate = System.currentTimeMillis()

                setOnShowListener {
                    getButton(BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(this@ReportLostItem, R.color.gold))
                    getButton(BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(this@ReportLostItem, R.color.gold))
                }
            }
            datePickerDialog.show()
        }

        cardDate.setOnClickListener { dateClickListener() }
        itemDateEditText.setOnClickListener { dateClickListener() }
    }

    private fun setupTimePicker() {
        itemTimeEditText.apply {
            inputType = InputType.TYPE_NULL
            showSoftInputOnFocus = false
            isFocusable = false
            isFocusableInTouchMode = false
            keyListener = null
        }

        val timeClickListener = {
            val contextThemeWrapper = ContextThemeWrapper(this, android.R.style.Theme_Holo_Dialog)

            val timePickerDialog = TimePickerDialog(
                contextThemeWrapper,
                { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    updateTimeField()
                    hideError(errorTime)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            ).apply {
                setOnShowListener {
                    getButton(BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(this@ReportLostItem, R.color.gold))
                    getButton(BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(this@ReportLostItem, R.color.gold))
                }
            }
            timePickerDialog.show()
        }

        cardTime.setOnClickListener { timeClickListener() }
        itemTimeEditText.setOnClickListener { timeClickListener() }
    }

    private fun setupPhotoSelection() {
        // Make entire card photo clickable
        cardPhoto.setOnClickListener {
            showImagePickerDialog() // This will now first show the NSFW warning
        }

        // Also keep the image view clickable for better UX
        photoImageView.setOnClickListener {
            showImagePickerDialog() // This will now first show the NSFW warning
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
        // First show the NSFW warning
        showNsfwWarning()
    }

    private fun showNsfwWarning() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_nsfw_warning, null)
        val builder = AlertDialog.Builder(this).apply {
            setView(dialogView)
            setCancelable(true)
        }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_agree).setOnClickListener {
            dialog.dismiss()
            // Now show the actual image picker options
            showImageSelectionOptions()
        }

        dialog.show()
    }

    private fun showImageSelectionOptions() {
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