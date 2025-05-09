package com.example.findit

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReportedItemsAdapter(
    private var items: List<ReportedItem>,
    private val onQrCodeClickListener: (ReportedItem) -> Unit,
    private val onDeleteClickListener: (ReportedItem) -> Unit
) : RecyclerView.Adapter<ReportedItemsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.item_image)
        val itemName: TextView = itemView.findViewById(R.id.item_name)
        val itemDetails: TextView = itemView.findViewById(R.id.item_details)
        val timeFound: TextView = itemView.findViewById(R.id.time_found)
        val place: TextView = itemView.findViewById(R.id.place)
        val iconClaimed: ImageView = itemView.findViewById(R.id.icon_claimed)
        val iconDelete: ImageView = itemView.findViewById(R.id.icon_delete)
        val qrContainer: LinearLayout = itemView.findViewById(R.id.qr_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reported_and_claimed_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // Set item data
        holder.itemName.text = item.itemName
        holder.itemDetails.text = "Item Details: ${item.description}"
        holder.timeFound.text = "Time Found: ${item.time}"
        holder.place.text = "Place: ${item.place}"

        // Set image if available
        item.imageResource?.let {
            holder.itemImage.setImageResource(it)
        }

        // Handle status-specific UI elements
        when (item.status) {
            ReportedItemStatus.CLAIMED -> {
                // For claimed items, only show tick mark
                holder.iconClaimed.visibility = View.VISIBLE
                holder.iconDelete.visibility = View.GONE
                holder.qrContainer.visibility = View.GONE
            }
            ReportedItemStatus.UNCLAIMED -> {
                // For unclaimed items, show both delete and QR options
                holder.iconClaimed.visibility = View.GONE
                holder.iconDelete.visibility = View.VISIBLE
                holder.qrContainer.visibility = View.VISIBLE
            }
            ReportedItemStatus.DELETED -> {
                // For deleted items, only show delete icon
                holder.iconClaimed.visibility = View.GONE
                holder.iconDelete.visibility = View.VISIBLE
                holder.qrContainer.visibility = View.GONE
            }
        }

        // Always show delete icon for unclaimed items regardless of status
        // This ensures it's positioned at the end of item details
        if (item.status == ReportedItemStatus.UNCLAIMED) {
            holder.iconDelete.visibility = View.VISIBLE
        }

        // Set click listeners for item
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            navigateToItemDetails(context, item)
        }

        // Set click listener for QR code
        holder.qrContainer.setOnClickListener {
            onQrCodeClickListener(item)
        }

        // Set click listener for delete icon
        holder.iconDelete.setOnClickListener {
            onDeleteClickListener(item)
        }
    }

    private fun navigateToItemDetails(context: Context, item: ReportedItem) {
        val intent = Intent(context, ItemDetails::class.java).apply {
            putExtra("item_name", item.itemName)
            putExtra("date", item.date)
            putExtra("time", item.time)
            putExtra("place", item.place)
            putExtra("image_resource", item.imageResource)
            putExtra("description", item.description)
            putExtra("status", item.status.toString())
        }
        context.startActivity(intent)
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<ReportedItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}