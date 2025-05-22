package com.example.findit

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.util.EnumMap

class QRGeneratorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrgenerator)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Get extras from intent
        val itemName = intent.getStringExtra("item_name") ?: "Unknown Item"
        val dataUri = intent.getStringExtra("datauri")

        // Set item name in UI
        findViewById<TextView>(R.id.item_name).text = itemName

        // Generate QR code

        // Display QR code
        val qrImageView = findViewById<ImageView>(R.id.qr_code_image)
        displayQrFromDataUri(dataUri,qrImageView)

        // Set up close button
        findViewById<Button>(R.id.btn_close).setOnClickListener {
            finish()
        }
    }

    fun displayQrFromDataUri(dataUri: String?, imageView: ImageView) {
        // Strip off the "data:image/png;base64," part if present
        if(dataUri==null){
            Toast.makeText(this@QRGeneratorActivity,"Error Generating QR", Toast.LENGTH_SHORT).show()
        }
        val base64Image = dataUri?.substringAfter("base64,")
        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        imageView.setImageBitmap(bitmap)
    }
}