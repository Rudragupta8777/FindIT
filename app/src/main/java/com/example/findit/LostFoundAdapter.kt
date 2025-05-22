package com.example.findit

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.findit.data.Item
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class LostFoundAdapter(private var allItems: List<Item>) :
    RecyclerView.Adapter<LostFoundAdapter.ViewHolder>() {

    // Current filtered list of items
    private var filteredItems: List<Item> = allItems

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
        holder.itemNameValue.text = item.title
        val (date, time) = formatDateTime(item.dateFound)
        holder.dateValue.text = date
        holder.timeValue.text = time
        holder.placeValue.text = item.location

        // Set image if available
        item.imageUrl.let {
            Glide.with(holder.itemView.context)
                .load(it)
                .into(holder.itemImage)
        }

        // Set click listener on the entire item view
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            navigateToItemDetails(context, item)
        }
    }

    private fun navigateToItemDetails(context: Context, item: Item) {
        val intent = Intent(context, ItemDetails::class.java).apply {
            putExtra("item_name", item.title)
            val (date, time) = formatDateTime(item.dateFound)
            putExtra("date", date)
            putExtra("time", time)
            putExtra("place", item.location)
            putExtra("image_resource", item.imageUrl)

            // You can add more data here if needed
            putExtra("contact", item.contact)
            putExtra("description", item.description)

            // Add the reported by user name
            putExtra("reported_by", item.postedBy.name ?: "Unknown User")
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
                item.title.lowercase(Locale.getDefault()).contains(lowercaseQuery) ||
                        item.location.lowercase(Locale.getDefault()).contains(lowercaseQuery)
            }
        }

        notifyDataSetChanged()
    }

    // Method to update all items
    fun updateItems(newItems: List<Item>) {
        allItems = newItems
        filteredItems = newItems
        notifyDataSetChanged()
    }

    fun formatDateTime(dateFound: String): Pair<String, String> {
        val instant = Instant.parse(dateFound)
        val zonedDateTime = instant.atZone(ZoneId.systemDefault()) // or use ZoneId.of("UTC") if you want UTC time

        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy")
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

        val date = zonedDateTime.format(dateFormatter)
        val time = zonedDateTime.format(timeFormatter)

        return date to time
    }
}