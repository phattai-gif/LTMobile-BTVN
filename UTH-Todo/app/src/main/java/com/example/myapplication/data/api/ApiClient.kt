package com.example.myapplication.data.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    const val BASE_URL = "https://share.tryapps.store/mock/teach/"

    /**
     * MOCK_API_TOKEN dùng cho Authorization: Bearer <token>.
     * Có thể thay đổi runtime hoặc lấy từ biến cấu hình.
     */
    var mockApiToken: String = ""

    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    val taskApiService: TaskApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(TaskApiService::class.java)
    }

    fun authHeader(): String {
        return if (mockApiToken.isNotBlank()) "Bearer $mockApiToken" else "Bearer demo_token"
    }
}
