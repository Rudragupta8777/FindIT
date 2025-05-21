package com.example.findit.apiService

import com.example.findit.data.ClaimStatusResponse
import com.example.findit.data.QRCodeResponse
import com.example.findit.data.SingleItemResponse
import com.example.findit.data.TokenRequest

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ClaimApiService {
    @POST("claim/generate-qr/{itemId}")
    suspend fun generateQRCode(@Path("itemId") itemId: String): Response<QRCodeResponse>

    @POST("claim/claim-item")
    suspend fun claimItem(@Body tokenRequest: TokenRequest): Response<SingleItemResponse>

    @GET("claim/status/{itemId}")
    suspend fun getClaimStatus(@Path("itemId") itemId: String): Response<ClaimStatusResponse>
}