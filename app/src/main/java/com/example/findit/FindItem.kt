package com.example.findit

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ListPopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.findit.objects.RetrofitInstance
import kotlinx.coroutines.launch
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.findit.Adapter.LostFoundAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class FindItem : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LostFoundAdapter
    private lateinit var searchEditText: EditText
    private lateinit var loaderOverlay: FrameLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var searchJob: Job? = null
    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false
    private val limit = 10
    private var selectedCategory: String? = null

    private val categories = arrayOf(
        "All Items",
        "Electronics",
        "ID Card",
        "Clothing",
        "Books",
        "Accessories",
        "Others"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_item)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        loaderOverlay = findViewById(R.id.loader_overlay)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.navigation_bar_color)
            window.statusBarColor = ContextCompat.getColor(this, R.color.header)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        // Initialize UI elements
        val home = findViewById<ImageView>(R.id.btn_home)
        val filter = findViewById<ImageView>(R.id.filter)
        searchEditText = findViewById(R.id.search)
        recyclerView = findViewById(R.id.recycler_view)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh)

        swipeRefreshLayout.setOnRefreshListener {
            refreshItems()
        }

        home.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }

        filter.setOnClickListener {
            showCategoryFilterPopup()
        }

        setupRecyclerView()
        setupSearch()

        loadInitialItems(isFromSearch = false, isFromRefresh = false)
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(500L) // 500ms debounce delay
                    // This flag prevents the loader from showing on search
                    loadInitialItems(isFromSearch = true, isFromRefresh = false)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Not needed
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = LostFoundAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(this@FindItem)
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= limit) {
                        loadMoreItems()
                    }
                }
            }
        })
    }

    private fun showCategoryFilterPopup() {
        val filterIcon = findViewById<ImageView>(R.id.filter)
        val listPopupWindow = ListPopupWindow(this)

        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categories) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(Color.WHITE)
                // Set padding (left, top, right, bottom)
                view.setPadding(48, 24, 48, 24)
                return view
            }
        }

        listPopupWindow.setAdapter(adapter)
        listPopupWindow.anchorView = filterIcon

        // Set width
        val widthPx = (250 * resources.displayMetrics.density).toInt() // 250dp
        listPopupWindow.width = widthPx
        listPopupWindow.height = ListPopupWindow.WRAP_CONTENT

        val backgroundDrawable = GradientDrawable().apply {
            setColor(Color.parseColor("#2E2D2D")) // Dark background
            cornerRadius = 12f // Rounded corners
            setStroke(3, Color.parseColor("#6FFFEEC3"))
        }
        listPopupWindow.setBackgroundDrawable(backgroundDrawable)

        // Set click listener
        listPopupWindow.setOnItemClickListener { _, _, position, _ ->
            selectedCategory = if (position == 0) {
                null // "All Items"
            } else {
                categories[position]
            }

            loadInitialItems(isFromSearch = true, isFromRefresh = false)
            listPopupWindow.dismiss()
        }

        listPopupWindow.show()
    }

    private fun loadInitialItems(isFromSearch: Boolean = false, isFromRefresh: Boolean = false) {
        if (!isFromSearch && !isFromRefresh) {
            loaderOverlay.visibility = View.VISIBLE
        }

        currentPage = 1
        isLastPage = false
        val searchQuery = searchEditText.text.toString().ifEmpty { null }
        fetchItems(currentPage, searchQuery, selectedCategory)
    }

    private fun loadMoreItems() {
        currentPage++
        val searchQuery = searchEditText.text.toString().ifEmpty { null }
        fetchItems(currentPage, searchQuery, selectedCategory)
    }

    private fun fetchItems(page: Int, searchQuery: String?, category: String?) {
        if (isLoading) return
        isLoading = true

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.authItemApi.getItems(
                    page = page,
                    limit = limit,
                    status = "found",
                    search = searchQuery,
                    category = category
                )

                if (response.isSuccessful) {
                    response.body()?.let { itemResponse ->
                        val newItems = itemResponse.items
                        if (newItems.isEmpty() || newItems.size < limit) {
                            isLastPage = true
                        }

                        if (page == 1) {
                            adapter.updateItems(newItems)
                        } else {
                            adapter.addItems(newItems)
                        }
                    } ?: run {
                        Log.e("fetchItems", "Response body is null")
                        if (page == 1) adapter.updateItems(emptyList())
                    }
                } else {
                    Log.e("fetchItems", "Response not successful: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("fetchItems", "Exception: ${e.message}", e)
            } finally {
                isLoading = false
                loaderOverlay.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun refreshItems() {
        loadInitialItems(isFromSearch = false, isFromRefresh = true)
    }
}