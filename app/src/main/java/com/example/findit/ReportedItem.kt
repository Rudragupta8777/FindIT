package com.example.findit

data class ReportedItem(
    val itemName: String,
    val date: String,
    val time: String,
    val place: String,
    val imageResource: Int? = null,
    val description: String = "",
    val status: ReportedItemStatus = ReportedItemStatus.UNCLAIMED
)