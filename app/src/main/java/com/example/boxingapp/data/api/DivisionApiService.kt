package com.example.boxingapp.data.remote

import com.example.boxingapp.data.model.Division
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DivisionApiService {
    @GET("v1/divisions")
    suspend fun getAllDivisions(): Response<List<Division>>

    @GET("v1/divisions/{id}")
    suspend fun getDivisionById(@Path("id") id: String): Response<Division>
}
