package com.example.findit

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ItemReportHistory : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReportedItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item_report_history)

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
        // Initialize the adapter with sample data and click listeners
        adapter = ReportedItemsAdapter(
            createSampleReportedItems(),
            onQrCodeClickListener = { item ->
                // Handle QR code click
                showQrCode(item)
            },
            onDeleteClickListener = { item ->
                // Handle delete click
                handleDeleteItem(item)
            }
        )

        // Set up the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun createSampleReportedItems(): List<ReportedItem> {
        // Create sample reported items with different statuses
        return listOf(
            ReportedItem(
                itemName = "Wallet",
                date = "05/05/25",
                time = "14:30",
                place = "Library",
                imageResource = R.drawable.placeholder,
                description = "Brown leather wallet with ID card",
                status = ReportedItemStatus.CLAIMED
            ),
            ReportedItem(
                itemName = "Laptop Charger",
                date = "04/05/25",
                time = "10:15",
                place = "F Block 603",
                imageResource = R.drawable.placeholder,
                description = "Dell laptop charger",
                status = ReportedItemStatus.UNCLAIMED
            ),
            ReportedItem(
                itemName = "Water Bottle",
                date = "03/05/25",
                time = "09:45",
                place = "Cafeteria",
                imageResource = R.drawable.placeholder,
                description = "Blue metal water bottle",
                status = ReportedItemStatus.CLAIMED
            ),
            ReportedItem(
                itemName = "Calculator",
                date = "02/05/25",
                time = "16:20",
                place = "Library",
                imageResource = R.drawable.placeholder,
                description = "Scientific calculator",
                status = ReportedItemStatus.UNCLAIMED
            )
        )
    }

    private fun showQrCode(item: ReportedItem) {
        // Launch QR code generator activity
        val intent = Intent(this, QRGeneratorActivity::class.java).apply {
            putExtra("item_name", item.itemName)
            putExtra("item_id", "12345") // Replace with actual item ID
        }
        startActivity(intent)
    }

    private fun handleDeleteItem(item: ReportedItem) {
        // In a real app, this would delete the item from database
        Toast.makeText(
            this,
            "Item '${item.itemName}' has been deleted",
            Toast.LENGTH_SHORT
        ).show()

        // Refresh the list (in a real app, you would fetch the new list from database)
        val updatedList = createSampleReportedItems().filter { it.itemName != item.itemName }
        adapter.updateItems(updatedList)
    }
}