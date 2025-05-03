package com.example.findit

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class LostFoundAdapter(private var allItems: List<LostFoundItem>) :
    RecyclerView.Adapter<LostFoundAdapter.ViewHolder>() {

    // Current filtered list of items
    private var filteredItems: List<LostFoundItem> = allItems

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.item_image)
        val itemNameValue: TextView = itemView.findViewById(R.id.item_name_value)
        val dateValue: TextView = itemView.findViewById(R.id.date_value)
        val timeValue: TextView = itemView.findViewById(R.id.time_value)
        val placeValue: TextView = itemView.findViewById(R.id.place_value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lost_found, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredItems[position]

        holder.itemNameValue.text = item.itemName
        holder.dateValue.text = item.date
        holder.timeValue.text = item.time
        holder.placeValue.text = item.place

        // Set image if available
        item.imageResource?.let {
            holder.itemImage.setImageResource(it)
        }

        // Set click listener on the entire item view
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            navigateToItemDetails(context, item)
        }
    }

    private fun navigateToItemDetails(context: Context, item: LostFoundItem) {
        val intent = Intent(context, ItemDetails::class.java).apply {
            putExtra("item_name", item.itemName)
            putExtra("date", item.date)
            putExtra("time", item.time)
            putExtra("place", item.place)
            putExtra("image_resource", item.imageResource)

            // You can add more data here if needed
            putExtra("contact", "Contact information for this item")
            putExtra("description", "This is a detailed description of the item that was found. It includes information about the condition and identifying features.")
        }
        context.startActivity(intent)
    }

    override fun getItemCount(): Int = filteredItems.size

    // Method to filter items based on search query
    fun filter(query: String) {
        val lowercaseQuery = query.lowercase(Locale.getDefault())

        filteredItems = if (query.isEmpty()) {
            allItems
        } else {
            allItems.filter { item ->
                item.itemName.lowercase(Locale.getDefault()).contains(lowercaseQuery) ||
                        item.place.lowercase(Locale.getDefault()).contains(lowercaseQuery)
            }
        }

        notifyDataSetChanged()
    }

    // Method to update all items
    fun updateItems(newItems: List<LostFoundItem>) {
        allItems = newItems
        filteredItems = newItems
        notifyDataSetChanged()
    }
}