package com.example.findit

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.findit.objects.RetrofitInstance
import kotlinx.coroutines.launch

class FindItem : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LostFoundAdapter
    private lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_find_item)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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

    private fun setupRecyclerView() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.authItemApi.getItems()
                if (response.isSuccessful) {
                    response.body()?.let { itemResponse ->
                        adapter = LostFoundAdapter(itemResponse.items)
                        recyclerView.layoutManager = LinearLayoutManager(this@FindItem)
                        recyclerView.adapter = adapter
                    } ?: run {
                        // Handle case where body is null
                        Log.e("setupRecyclerView1", "Response body is null")
                    }
                } else {
                    Log.e("setupRecyclerView1", "Response not successful: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("setupRecyclerView1", "Exception: ${e.message}", e)
            }
        }
    }

}