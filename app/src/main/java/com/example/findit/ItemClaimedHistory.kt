package com.example.findit

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.findit.ItemReportHistory
import com.example.findit.objects.RetrofitInstance
import kotlinx.coroutines.launch

class ItemClaimedHistory : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClaimedItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item_claimed_history)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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



    private fun setupRecyclerView(){
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
            }
        }
    }


}