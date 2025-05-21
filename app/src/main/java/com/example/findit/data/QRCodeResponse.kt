package com.example.findit.data

data class QRCodeResponse(
    val message: String,
    val qrCode: String,
    val expiresAt: String
)