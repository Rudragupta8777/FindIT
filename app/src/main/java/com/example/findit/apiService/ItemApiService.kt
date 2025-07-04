package com.example.findit.apiService

import com.example.findit.data.GetReported
import com.example.findit.data.ItemResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import com.example.findit.data.SingleItemResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Query

interface ItemApiService {
    @Multipart
    @POST("item")
    suspend fun createItem(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("contact") contact: RequestBody,
        @Part("category") category: RequestBody,
        @Part("location") location: RequestBody,
        @Part("dateFound") dateFound: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<SingleItemResponse>

    @GET("item")
    suspend fun getItems(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("category") category: String? = null,
        @Query("status") status: String = "found",
        @Query("search") search: String? = null
    ): Response<ItemResponse>

    @GET("item/{id}")
    suspend fun getItemById(@Path("id") itemId: String): Response<SingleItemResponse>

    @GET("item/user/posts")
    suspend fun getUserPosts(): Response<GetReported>

    @GET("item/user/claims")
    suspend fun getUserClaims(): Response<GetReported>
}