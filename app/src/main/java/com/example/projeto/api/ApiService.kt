package com.example.projeto.api

import com.example.projeto.data.Conversation
import com.example.projeto.data.ConversationDto
import com.example.projeto.data.CreateConversationRequest
import com.example.projeto.data.FileUploadResponse
import com.example.projeto.data.LastMessageDto
import com.example.projeto.data.Product
import com.example.projeto.data.User
import com.example.projeto.model.AuthResponse
import com.example.projeto.model.LoginRequest
import com.example.projeto.model.LoginResponse
import com.example.projeto.model.RegisterRequest
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    // =====================GETTERS
    @GET("products/announce/user")
    suspend fun getMyProducts(): Response<List<Product>>

    @GET("products/announces")
    suspend fun getProducts(): Response<List<Product>>

    @GET("users/profile")
    suspend fun getProfile(): Response<User>

    @GET("users/conversations")
    fun getConversations(): Call<List<ConversationDto>>

    @GET("conversation/{conversationId}/messages")
    fun getMessages(
        @Path("conversationId") conversationId: String
    ): Call<List<LastMessageDto>>

    // =====================POSTS
    @POST("users/register")
    suspend fun register(
        @Body user: RegisterRequest
    ): Response<AuthResponse>

    @POST("users/login")
    suspend fun login(
        @Body user: LoginRequest
    ): Response<AuthResponse>

    @Multipart
    @POST("products/announce")
    suspend fun announceProduct(
        @Part("Title") title: RequestBody,
        @Part("Location") location: RequestBody,
        @Part("Description") description: RequestBody?,
        @Part("State") state: RequestBody,
        @Part("Quantity") quantity: RequestBody,
        @Part("Category") category: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Response<JsonObject>

    @Multipart
    @POST("conversation/{conversationId}/file")
    fun uploadFile(
        @Path("conversationId") conversationId: String,
        @Part file: MultipartBody.Part
    ): Call<FileUploadResponse>

    @POST("conversation")
    suspend fun createConversation(
        @Body request: CreateConversationRequest
    ): Response<Conversation>

    // ================== PATCH
    @Multipart
    @PATCH("users/update")
    suspend fun updateProfile(
        @Part("Name") name: RequestBody?,
        @Part("Bio") bio: RequestBody?,
        @Part("Locale") locale: RequestBody?,
        @Part PhotoUrl: MultipartBody.Part?
    ): Response<User>
}
