package com.example.boxingapp.data.api

import com.example.boxingapp.data.api.BoxingApiService
import com.example.boxingapp.data.remote.DivisionApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://boxing-data-api.p.rapidapi.com/"
    private const val API_KEY = "6eda44e804mshbcabadf3acfd3e0p12a2c4jsn030cc63c5b5c"
    private const val API_HOST = "boxing-data-api.p.rapidapi.com"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-rapidapi-key", API_KEY)
                .addHeader("x-rapidapi-host", API_HOST)
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: BoxingApiService by lazy {
        retrofit.create(BoxingApiService::class.java)
    }

    val divisionApi: DivisionApiService by lazy {
        retrofit.create(DivisionApiService::class.java)
    }
}
