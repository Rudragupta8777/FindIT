package com.example.findit.apiService

import com.example.findit.data.UserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApiService {
    @POST("user/login")
    suspend fun loginUser(): Response<UserResponse>

    @GET("user")
    suspend fun getUserDetails(): Response<UserResponse>

    @GET("/")
    suspend fun serverStatus(): Response<Map<String,Any>>
}