package com.example.notimedi.api

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class Part(
    @SerializedName("text") val text: String
)

data class Content(
    @SerializedName("role") val role: String,
    @SerializedName("parts") val parts: List<Part>
)

data class GeminiRequest(
    @SerializedName("contents") val contents: List<Content>
)

data class GeminiResponse(
    @SerializedName("candidates") val candidates: List<Candidate>
)

data class Candidate(
    @SerializedName("content") val content: Content
)

interface GeminiApiService {
    @Headers("Content-Type: application/json")
    @POST("/v1beta/models/gemini-pro:generateContent")
    fun generateContent(
        @Body request: GeminiRequest
    ): Call<GeminiResponse>
}
