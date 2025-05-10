package com.example.findit.data



data class User(
    val _id: String,
    val email: String,
    val name: String,
    val regNo: String,
    val registeredItems: List<String>,
    val claimedItems: List<String>,
    val createdAt: String,
    val updatedAt: String
)
