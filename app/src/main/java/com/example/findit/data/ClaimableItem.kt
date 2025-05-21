package com.example.findit.data

data class ClaimableItem(
    val id: String,
    val title: String,
    val category: String,
    val status: String,
    val isClaimable: Boolean
)