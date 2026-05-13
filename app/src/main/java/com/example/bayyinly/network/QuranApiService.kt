package com.example.bayyinly.network

import com.example.bayyinly.model.ReciterResponse
import retrofit2.Response
import retrofit2.http.GET

interface QuranApiService {
    // This endpoint returns all audio editions (Reciters)
    @GET("v1/edition/format/audio")
    suspend fun getAvailableReciters(): Response<ReciterResponse>
}