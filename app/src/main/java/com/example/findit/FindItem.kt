package com.example.findit

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.findit.objects.RetrofitInstance
import kotlinx.coroutines.launch
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class FindItem : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LostFoundAdapter
    private lateinit var searchEditText: EditText
    private lateinit var loaderOverlay: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_find_item)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        loaderOverlay = findViewById(R.id.loader_overlay)

        // Initialize UI elements
        val home = findViewById<ImageView>(R.id.btn_home)
        searchEditText = findViewById(R.id.search)
        recyclerView = findViewById(R.id.recycler_view)
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)

        swipeRefreshLayout.setOnRefreshListener {
            refreshItems(swipeRefreshLayout)
        }

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
        loaderOverlay.visibility = View.VISIBLE // Show loader

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.authItemApi.getItems()
                if (response.isSuccessful) {
                    response.body()?.let { itemResponse ->
                        adapter = LostFoundAdapter(itemResponse.items)
                        recyclerView.layoutManager = LinearLayoutManager(this@FindItem)
                        recyclerView.adapter = adapter
                    } ?: run {
                        Log.e("setupRecyclerView", "Response body is null")
                    }
                } else {
                    Log.e("setupRecyclerView", "Response not successful: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("setupRecyclerView", "Exception: ${e.message}", e)
            } finally {
                loaderOverlay.visibility = View.GONE // Hide loader after data load
            }
        }
    }


    private fun refreshItems(swipeRefreshLayout: SwipeRefreshLayout) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.authItemApi.getItems()
                if (response.isSuccessful) {
                    response.body()?.let { itemResponse ->
                        adapter.updateItems(itemResponse.items)
                    }
                }
            } catch (e: Exception) {
                Log.e("refreshItems", "Exception: ${e.message}", e)
            } finally {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}