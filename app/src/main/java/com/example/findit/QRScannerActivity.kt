package com.example.findit

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.example.findit.data.TokenRequest
import com.example.findit.databinding.ActivityQrScannerBinding
import com.example.findit.objects.RetrofitInstance
import com.google.gson.Gson
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QRScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrScannerBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeScanner: BarcodeScanner
    private lateinit var lottieAnimationView: LottieAnimationView
    private var cameraStarted = false
    private var isProcessingQR = false
    private var itemId = "id"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityQrScannerBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(binding.root)

        itemId = intent.getStringExtra("itemId").toString()
        // Initialize lottie animation
        lottieAnimationView = binding.lottieAnimationView

        // Setup barcode scanner
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)

        // Initially hide the camera preview
        binding.previewView.visibility = View.GONE

        // Initially show a placeholder or instruction image
        binding.placeholderImage.visibility = View.VISIBLE

        // Set up the home button click listener
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
            finish() // Simply close this activity
        }

        // Set up the scan button
        binding.btnScanQr.setOnClickListener {
            if (!cameraStarted) {
                // Request camera permissions if not already granted
                if (allPermissionsGranted()) {
                    startCamera()
                    binding.previewView.visibility = View.VISIBLE
                    binding.placeholderImage.visibility = View.GONE
                    binding.btnScanQr.text = "Cancel Scan"
                    cameraStarted = true
                } else {
                    ActivityCompat.requestPermissions(
                        this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                    )
                }
            } else {
                // Stop camera and return to initial state
                stopCamera()
                binding.previewView.visibility = View.GONE
                binding.placeholderImage.visibility = View.VISIBLE
                binding.btnScanQr.text = "Scan QR Code"
                cameraStarted = false
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            // Image analysis
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, QRCodeAnalyzer { qrCodes ->
                        if (qrCodes.isNotEmpty() && !isProcessingQR) {
                            isProcessingQR = true
                            val qrCode = qrCodes.first()
                            qrCode.rawValue?.let { value ->
                                val token = TokenRequest(value, itemId)
                                Log.d("Claim API", "Request Body: ${Gson().toJson(token)}")


                                lifecycleScope.launch {
                                    try{
                                        val response = RetrofitInstance.authClaimApi.claimItem(token)
                                        Log.e("Claim Api", "Response : ${response}")// 🔁 Your suspend function
                                        if (response.isSuccessful) {
                                            // You can parse response.body() if needed
                                            navigateToSuccessScreen(value)
                                        }else{
                                            Toast.makeText(this@QRScannerActivity, "API Error: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                                            isProcessingQR = false // Allow retry
                                        }
                                    }catch (e: Exception) {
                                        Toast.makeText(this@QRScannerActivity, "Request failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                        isProcessingQR = false // Allow retry
                                    }

                                    // Optional: Stop scanning after successful detection
                                    stopCamera()
                                    binding.previewView.visibility = View.GONE
                                    binding.placeholderImage.visibility = View.VISIBLE
                                    binding.btnScanQr.text = "Scan QR Code"
                                    cameraStarted = false
                                    isProcessingQR = false
                                }
                            }
                        }
                    })
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun navigateToSuccessScreen(qrValue: String) {
        // You can pass the QR code value to the success screen if needed
        val intent = Intent(this, ItemClaimedSuccesfully::class.java)
        intent.putExtra("QR_CODE_VALUE", qrValue)
        startActivity(intent)
    }

    private fun stopCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
                binding.previewView.visibility = View.VISIBLE
                binding.placeholderImage.visibility = View.GONE
                binding.btnScanQr.text = "Cancel Scan"
                cameraStarted = true
            } else {
                Toast.makeText(this,
                    "Camera permission is required to scan QR codes.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        barcodeScanner.close()
    }

    inner class QRCodeAnalyzer(private val onQRCodesDetected: (qrCodes: List<Barcode>) -> Unit) : ImageAnalysis.Analyzer {

        @androidx.camera.core.ExperimentalGetImage
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                barcodeScanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isNotEmpty()) {
                            onQRCodesDetected(barcodes)
                        }
                    }
                    .addOnFailureListener {
                        Log.e(TAG, "Barcode scanning failed: ${it.message}", it)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }
    }

    companion object {
        private const val TAG = "QRScannerActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}