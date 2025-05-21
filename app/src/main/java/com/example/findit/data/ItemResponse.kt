package com.example.findit.data

data class ItemResponse(
    val message: String,
    val items: List<Item>,
    val totalPages: Int,
    val currentPage: Int,
    val total: Int
)
