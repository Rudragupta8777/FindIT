package com.example.findit

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.findit.FindItem
import com.example.findit.SplashScreen
import com.example.findit.data.ItemPost
import com.example.findit.objects.RetrofitInstance
import kotlinx.coroutines.launch

class ItemReportHistory : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReportedItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item_report_history)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Initialize UI elements
        val backButton = findViewById<ImageView>(R.id.btn_back)
        val homeButton = findViewById<ImageView>(R.id.btn_home)
        recyclerView = findViewById(R.id.recycler_view)

        // Set up navigation buttons
        backButton.setOnClickListener {
            finish() // Go back to previous screen
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Clear activity stack
            startActivity(intent)
            finish()
        }

        // Set up RecyclerView
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.authItemApi.getUserPosts()
                if (response.isSuccessful) {
                    response.body()?.let { getReported ->
                        adapter = ReportedItemsAdapter(
                            getReported.items,
                            onQrCodeClickListener = { item, view ->
                                showQrCode(item, view)
                            },
//                            onDeleteClickListener = { item ->
//                                handleDeleteItem(item)
//                            }
                        )

                        // Set up the RecyclerView
                        recyclerView.layoutManager = LinearLayoutManager(this@ItemReportHistory)
                        recyclerView.adapter = adapter
                    } ?: run {
                        // Handle case where body is null
                        Log.e("setupRecyclerView", "Response body is null")
                    }
                } else {
                    Log.e("setupRecyclerView", "Response not successful: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("setupRecyclerView", "Exception: ${e.message}", e)
            }
        }
    }

    private fun showQrCode(item: ItemPost, view: View) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.authClaimApi.generateQRCode(item._id)
                if (response.isSuccessful && response.body() != null) {
                    val uri = response.body()?.qrCode
                    val intent = Intent(this@ItemReportHistory, QRGeneratorActivity::class.java).apply {
                        putExtra("item_name", item.title)
                        putExtra("datauri", uri)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@ItemReportHistory,
                        "Sorry, try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ItemReportHistory,
                    "Exception ❌: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                view.isEnabled = true // 🔓 Always re-enable, success or error
            }
        }
    }


    private fun handleDeleteItem(item: ItemPost) {
        // Create dialog using custom layout
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_delete_confirmation)

        // Make dialog background transparent and set rounded corners
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }

        // Get references to views in the custom layout
        val dialogTitle = dialog.findViewById<TextView>(R.id.dialog_title)
        val dialogMessage = dialog.findViewById<TextView>(R.id.dialog_message)
        val btnYes = dialog.findViewById<Button>(R.id.btn_yes)
        val btnNo = dialog.findViewById<Button>(R.id.btn_no)

        // Set custom message
        dialogMessage.text = "Are you sure you want to delete '${item.title}'?"

        // Set button click listeners
        btnYes.setOnClickListener {
            // Delete the item
            // In a real app, this would delete the item from database
            Toast.makeText(
                this,
                "Item '${item.title}' has been deleted",
                Toast.LENGTH_SHORT
            ).show()

            // Refresh the list (in a real app, you would fetch the new list from database)
            //val updatedList = createSampleReportedItems().filter { it.itemName != item.itemName }
            //adapter.updateItems(updatedList)

            dialog.dismiss()
        }

        btnNo.setOnClickListener {
            // User canceled the deletion
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
    }
}