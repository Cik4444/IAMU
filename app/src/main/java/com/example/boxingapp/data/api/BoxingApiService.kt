package com.example.boxingapp.data.api

import com.example.boxingapp.data.model.Fighter
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.Response


interface BoxingApiService {
    @GET("v1/fighters")
    suspend fun getFighters(
        @Query("name") name: String,
        @Query("page_num") page: Int = 1,
        @Query("page_size") size: Int = 50
    ): Response<List<Fighter>>

    /**
     * Endpoint for performing an exact fighter search. The API returns a
     * 400 error when no matching fighter is found, so the repository handles
     * this case and falls back to cached data.
     */
    @GET("v1/fighters/search")
    suspend fun searchFighters(
        @Query("name") name: String,
        @Query("page_num") page: Int = 1,
        @Query("page_size") size: Int = 50
    ): Response<List<Fighter>>

}
