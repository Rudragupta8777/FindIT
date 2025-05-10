package com.example.findit

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.util.EnumMap

class QRGeneratorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrgenerator)

        // Get extras from intent
        val itemName = intent.getStringExtra("item_name") ?: "Unknown Item"
        val itemId = intent.getStringExtra("item_id") ?: "12345"

        // Set item name in UI
        findViewById<TextView>(R.id.item_name).text = itemName

        // Generate QR code
        val qrCodeBitmap = generateQRCode("FINDIT_ITEM_$itemId", 512)

        // Display QR code
        val qrImageView = findViewById<ImageView>(R.id.qr_code_image)
        qrImageView.setImageBitmap(qrCodeBitmap)

        // Set up close button
        findViewById<Button>(R.id.btn_close).setOnClickListener {
            finish()
        }
    }

    private fun generateQRCode(content: String, size: Int): Bitmap? {
        try {
            // Hints for QR code generation
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            hints[EncodeHintType.MARGIN] = 1 // Default is 4

            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)

            // Create bitmap
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}