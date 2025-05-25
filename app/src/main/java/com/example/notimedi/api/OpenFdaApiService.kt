package com.example.notimedi.api

import retrofit2.http.GET
import retrofit2.http.Query

data class DrugInfoResponse(
    val results: List<DrugResult>
)

data class DrugResult(
    val purpose: List<String>?,
    val indications_and_usage: List<String>?,
    val warnings: List<String>?,
    val adverse_reactions: List<String>?
)

interface OpenFdaApiService {
    @GET("drug/label.json")
    suspend fun searchDrug(
        @Query("search") query: String,
        @Query("limit") limit: Int = 1
    ): DrugInfoResponse
}
