package com.example.findit

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.findit.data.ItemPost
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ReportedItemsAdapter(
    private var items: List<ItemPost>,
    private val onQrCodeClickListener: (ItemPost, View) -> Unit,
//    private val onDeleteClickListener: (ItemPost) -> Unit
    // Removed delete functionality
) : RecyclerView.Adapter<ReportedItemsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.item_image)
        val itemName: TextView = itemView.findViewById(R.id.item_name_value)
        val date: TextView = itemView.findViewById(R.id.date_value)
        val timeFound: TextView = itemView.findViewById(R.id.time_value)
        val place: TextView = itemView.findViewById(R.id.place_value)
        val iconClaimed: ImageView = itemView.findViewById(R.id.icon_claimed)
//        Future Update
//        val iconDelete: ImageView = itemView.findViewById(R.id.icon_delete)
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
        holder.itemName.text = item.title
        val (date, time) = formatDateTime(item.dateFound)
        holder.date.text = date
        holder.timeFound.text = time
        holder.place.text = item.location

        // Set image if available
        item.imageUrl.let {
            Glide.with(holder.itemView.context)
                .load(it)
                .into(holder.itemImage)
        }

        // Handle status-specific UI elements and click behavior
        when (item.status) {
            "claimed" -> {
                // For claimed items, show tick mark and make card clickable
                holder.iconClaimed.visibility = View.VISIBLE
//                holder.iconDelete.visibility = View.GONE
                holder.qrContainer.visibility = View.GONE

                // Enable click to view details ONLY for claimed items
                holder.itemView.setOnClickListener {
                    val context = holder.itemView.context
                    navigateToClaimedItemDetails(context, item)
                }
                // Visual feedback for clickable items
                holder.itemView.isClickable = true
                holder.itemView.isFocusable = true
            }
            "found" -> {
                // For unclaimed items, show both delete and QR options
                holder.iconClaimed.visibility = View.GONE
//                holder.iconDelete.visibility = View.VISIBLE
                holder.qrContainer.visibility = View.VISIBLE

                // Disable click for unclaimed items
                holder.itemView.setOnClickListener(null)
                // Remove visual feedback for clickable items
                holder.itemView.isClickable = false
                holder.itemView.isFocusable = false
            }
            "deleted" -> {
                // For deleted items, only show delete icon
                holder.iconClaimed.visibility = View.GONE
//                holder.iconDelete.visibility = View.VISIBLE
                holder.qrContainer.visibility = View.GONE

                // Disable click for deleted items
                holder.itemView.setOnClickListener(null)
                // Remove visual feedback for clickable items
                holder.itemView.isClickable = false
                holder.itemView.isFocusable = false
            }
        }

        // Set click listener for QR code button
        holder.qrContainer.setOnClickListener {
            it.isEnabled = false
            onQrCodeClickListener(item, it)
        }


        // Set click listener for delete icon with confirmation dialog
//        holder.iconDelete.setOnClickListener {
//            onDeleteClickListener(item)
//        }
    }

    private fun navigateToClaimedItemDetails(context: Context, item: ItemPost) {
        val intent = Intent(context, ClaimedItemDetails::class.java).apply {
            putExtra("item_name", item.title)
            val (date, time) = formatDateTime(item.dateFound)
            putExtra("date", date)
            putExtra("time", time)
            putExtra("place", item.location)
            putExtra("image_resource", item.imageUrl)
            putExtra("description", item.description)
            putExtra("status", item.status)
            putExtra("contact",item.contact)
            putExtra("reportedBy",item.postedBy.name + " " + item.postedBy.regNo)
            putExtra("claimedBy",item.claimedBy.name + " " + item.claimedBy.regNo)
        }
        context.startActivity(intent)
    }

    override fun getItemCount(): Int = items.size

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