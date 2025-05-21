package com.example.notimedi.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeminiClient {

    private const val BASE_URL = "https://generativelanguage.googleapis.com"
    private const val API_KEY = "AIzaSyBH5YY-bHUjlWb0dpGkkD5osOMda6vY2BA"

    private val client = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val originalRequest = chain.request()
            val newUrl = originalRequest.url.newBuilder()
                .addQueryParameter("key", API_KEY)
                .build()
            val newRequest = originalRequest.newBuilder().url(newUrl).build()
            chain.proceed(newRequest)
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val apiService: GeminiApiService = retrofit.create(GeminiApiService::class.java)
}
