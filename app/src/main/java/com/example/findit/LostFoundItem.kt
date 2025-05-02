package com.example.findit

data class LostFoundItem(
    val itemName: String,
    val date: String,
    val time: String,
    val place: String,
    val imageResource: Int? = null, // Use this for drawable resources
    val contact: String = "",       // Contact information
    val description: String = ""    // Description of the item
)