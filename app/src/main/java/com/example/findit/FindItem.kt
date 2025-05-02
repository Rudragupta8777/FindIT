package com.example.findit

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FindItem : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LostFoundAdapter
    private lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_find_item)

        // Initialize UI elements
        val home = findViewById<ImageView>(R.id.btn_home)
        searchEditText = findViewById(R.id.search)
        recyclerView = findViewById(R.id.recycler_view)

        // Set home button click listener
        home.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }

        // Set up RecyclerView
        setupRecyclerView()

        // Set up search functionality
        setupSearch()
    }

    private fun setupRecyclerView() {
        // Create sample data
        val items = createSampleItems()

        // Set up adapter
        adapter = LostFoundAdapter(items)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Filter the list based on the search query
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // Not needed
            }
        })
    }

    private fun createSampleItems(): List<LostFoundItem> {
        // Create and return a list of sample items
        return listOf(
            LostFoundItem(
                itemName = "Lost Wallet",
                date = "03/08/24",
                time = "22:48",
                place = "F Block 603",
                imageResource = R.drawable.placeholder
            ),
            LostFoundItem(
                itemName = "Keys",
                date = "02/08/24",
                time = "14:30",
                place = "Library",
                imageResource = R.drawable.placeholder
            ),
            LostFoundItem(
                itemName = "Black Backpack",
                date = "01/08/24",
                time = "09:15",
                place = "Cafeteria",
                imageResource = R.drawable.placeholder
            ),
            LostFoundItem(
                itemName = "Water Bottle",
                date = "31/07/24",
                time = "16:45",
                place = "Gym",
                imageResource = R.drawable.placeholder
            )
        )
    }
}