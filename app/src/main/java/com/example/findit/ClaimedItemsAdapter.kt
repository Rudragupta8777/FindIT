package com.example.findit

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ClaimedItemsAdapter(private var claimedItems: List<ClaimedItem>) :
    RecyclerView.Adapter<ClaimedItemsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.item_image)
        val itemNameValue: TextView = itemView.findViewById(R.id.item_name_value)
        val dateValue: TextView = itemView.findViewById(R.id.date_value)
        val timeValue: TextView = itemView.findViewById(R.id.time_value)
        val placeValue: TextView = itemView.findViewById(R.id.place_value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_claimed_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = claimedItems[position]

        holder.itemNameValue.text = item.itemName
        holder.dateValue.text = item.date
        holder.timeValue.text = item.time
        holder.placeValue.text = item.place

        // Set image if available
        item.imageResource?.let {
            holder.itemImage.setImageResource(it)
        }

        // Set click listener to navigate to details
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            navigateToClaimedItemDetails(context, item)
        }
    }

    private fun navigateToClaimedItemDetails(context: Context, item: ClaimedItem) {
        val intent = Intent(context, ClaimedItemDetails::class.java).apply {
            putExtra("item_name", item.itemName)
            putExtra("date", item.date)
            putExtra("time", item.time)
            putExtra("place", item.place)
            putExtra("image_resource", item.imageResource)
            putExtra("reported_by", item.reportedBy)

            // You can add more data here if needed
            putExtra("contact", "Contact information for this item")
            putExtra("description", "This is a detailed description of the item that was claimed.")
        }
        context.startActivity(intent)
    }

    override fun getItemCount(): Int = claimedItems.size

    // Method to update the claimed items list
    fun updateItems(newItems: List<ClaimedItem>) {
        claimedItems = newItems
        notifyDataSetChanged()
    }
}