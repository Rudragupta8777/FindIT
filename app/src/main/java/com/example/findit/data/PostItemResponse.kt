package com.example.findit.data

data class PostItemResponse(
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
    val createdAt: String,
    val updatedAt: String
)
