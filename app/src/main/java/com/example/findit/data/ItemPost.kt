package com.example.findit.data

data class ItemPost(
    val _id: String,
    val title: String,
    val description: String,
    val contact: String,
    val category: String,
    val imageUrl: String,
    val status: String,
    val location: String,
    val dateFound: String,
    val postedBy: String,
    val claimedBy: String?,
    val claimedWhen: String?,
    val createdAt: String,
    val updatedAt: String
)
