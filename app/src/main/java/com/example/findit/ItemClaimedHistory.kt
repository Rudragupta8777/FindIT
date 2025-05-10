package com.example.findit

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ItemClaimedHistory : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClaimedItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item_claimed_history)

        // Initialize UI elements
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        val btnHome = findViewById<ImageView>(R.id.btn_home)
        recyclerView = findViewById(R.id.recycler_view)

        // Set back button click listener
        btnBack.setOnClickListener {
            finish() // Go back to previous screen
        }

        // Set home button click listener
        btnHome.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Clear activity stack
            startActivity(intent)
        }

        // Set up RecyclerView
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        // Create sample data or fetch from database/API
        val items = createSampleClaimedItems()

        // Set up adapter
        adapter = ClaimedItemsAdapter(items)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun createSampleClaimedItems(): List<ClaimedItem> {
        // This would be replaced with actual data from your database or API
        return listOf(
            ClaimedItem(
                itemName = "Calculator",
                date = "05/08/24",
                time = "13:20",
                place = "Room 401",
                imageResource = R.drawable.placeholder,
                claimedDate = "07/08/24",
                reportedBy = "John Doe"
            ),
            ClaimedItem(
                itemName = "ID Card",
                date = "04/08/24",
                time = "09:45",
                place = "Main Lobby",
                imageResource = R.drawable.placeholder,
                claimedDate = "06/08/24",
                reportedBy = "Alice Smith"
            ),
            ClaimedItem(
                itemName = "Headphones",
                date = "03/08/24",
                time = "16:30",
                place = "Computer Lab",
                imageResource = R.drawable.placeholder,
                claimedDate = "05/08/24",
                reportedBy = "Bob Johnson"
            )
        )
    }
}