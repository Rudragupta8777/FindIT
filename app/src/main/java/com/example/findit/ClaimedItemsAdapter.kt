package com.example.findit

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.findit.data.ItemPost
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ClaimedItemsAdapter(private var claimedItems: List<ItemPost>) :
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

        // Set click listener to navigate to details
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            navigateToClaimedItemDetails(context, item)
        }
    }

    private fun navigateToClaimedItemDetails(context: Context, item: ItemPost) {
        val intent = Intent(context, ClaimedItemDetails::class.java).apply {
            putExtra("item_name", item.title)
            val (date, time) = formatDateTime(item.dateFound)
            putExtra("date", date)
            putExtra("time", time)
            putExtra("place", item.location)
            putExtra("image_resource", item.imageUrl)
            putExtra("reportedBy",item.postedBy.name + " " + item.postedBy.regNo)
            putExtra("claimedBy",item.claimedBy.name + " " + item.claimedBy.regNo)

            // You can add more data here if needed
            putExtra("contact", item.contact)
            putExtra("description", item.description)
            putExtra("image_resource",item.imageUrl)
        }
        context.startActivity(intent)
    }

    override fun getItemCount(): Int = claimedItems.size

    // Method to update the claimed items list
    fun updateItems(newItems: List<ItemPost>) {
        claimedItems = newItems
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