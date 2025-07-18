package com.example.findit

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.findit.ItemReportHistory
import com.example.findit.objects.RetrofitInstance
import kotlinx.coroutines.launch

class ItemClaimedHistory : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClaimedItemsAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var loaderOverlay: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item_claimed_history)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        loaderOverlay = findViewById(R.id.loader_overlay)

        // Initialize UI elements
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        val btnHome = findViewById<ImageView>(R.id.btn_home)
        recyclerView = findViewById(R.id.recycler_view)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh)

        swipeRefreshLayout.setOnRefreshListener {
            refreshItems()
        }


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



    private fun setupRecyclerView(){
        loaderOverlay.visibility = View.VISIBLE // Show loader

        lifecycleScope.launch{
            try{
                val response = RetrofitInstance.authItemApi.getUserClaims()
                if (response.isSuccessful) {
                    response.body()?.let { getReported ->
                        adapter = ClaimedItemsAdapter(getReported.items)
                        // Set up the RecyclerView
                        recyclerView.layoutManager = LinearLayoutManager(this@ItemClaimedHistory)
                        recyclerView.adapter = adapter
                    } ?: run {
                        // Handle case where body is null
                        Log.e("setupRecyclerView", "Response body is null")
                    }
                } else {
                    Log.e("setupRecyclerView", "Response not successful: ${response.code()}")
                }
            }catch (e : Exception){
                Log.e("setupRecyclerView", "Exception: ${e.message}", e)
            }finally {
                loaderOverlay.visibility = View.GONE // Hide loader after data load
            }
        }
    }

    private fun refreshItems() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.authItemApi.getUserClaims()
                if (response.isSuccessful) {
                    response.body()?.let { getReported ->
                        adapter.updateItems(getReported.items)
                    } ?: run {
                        Log.e("refreshItems", "Response body is null")
                    }
                } else {
                    Log.e("refreshItems", "Response not successful: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("refreshItems", "Exception: ${e.message}", e)
            } finally {
                // ✅ Make sure this line is here, not inside the adapter
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

}