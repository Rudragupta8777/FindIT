package com.example.findit.objects

import android.util.Log
import com.example.findit.apiService.ClaimApiService
import com.example.findit.apiService.ItemApiService
import com.example.findit.apiService.UserApiService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val TAG = "RetrofitInstance"
    private const val BASE_URL = "https://findit-backend.azurewebsites.net/"

    // Initialize public Retrofit instance immediately
    private val publicClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val publicUserApi: UserApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(publicClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(UserApiService::class.java)

    // OkHttpClient with dynamic Firebase token interceptor
    private val authClient = OkHttpClient.Builder()
        .addInterceptor(FirebaseAuthInterceptor())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Pre-create authenticated APIs using the dynamic interceptor
    private val authRetrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(authClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Exposed authenticated APIs
    val authItemApi: ItemApiService = authRetrofit.create(ItemApiService::class.java)
    val authClaimApi: ClaimApiService = authRetrofit.create(ClaimApiService::class.java)
    val authUserApi: UserApiService = authRetrofit.create(UserApiService::class.java)

    // Dynamic interceptor that fetches a fresh Firebase token for each request
    private class FirebaseAuthInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val originalRequest = chain.request()

            // Check if user is signed in
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                Log.w(TAG, "No user signed in, proceeding without authentication")
                return chain.proceed(originalRequest)
            }

            // We need to make this synchronous for OkHttp
            // This is a blocking operation, but necessary for the interceptor
            val token = try {
                kotlinx.coroutines.runBlocking {
                    currentUser.getIdToken(true).await().token ?: ""
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get Firebase token", e)
                ""
            }

            // If we got a token, add it to the request
            return if (token.isNotEmpty()) {
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
                Log.d(TAG, "Adding fresh Firebase token to request")
                chain.proceed(newRequest)
            } else {
                Log.w(TAG, "Proceeding with request without token")
                chain.proceed(originalRequest)
            }
        }
    }
}