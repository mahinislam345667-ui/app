package com.example.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun analyzeImage(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}
