package com.example.findit

data class ClaimedItem(
    val itemName: String,
    val date: String,
    val time: String,
    val place: String,
    val imageResource: Int? = null,
    val claimedDate: String = "",    // Date when the item was claimed
    val reportedBy: String = "",     // Who reported finding the item
    val contact: String = "",        // Contact information of the reporter
    val description: String = ""     // Description of the item
)