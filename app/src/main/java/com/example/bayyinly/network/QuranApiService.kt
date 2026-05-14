package com.example.bayyinly.network

import com.example.bayyinly.model.ReciterResponse
import retrofit2.Response
import retrofit2.http.GET

interface QuranApiService {
    // This endpoint returns all audio editions (Reciters)
    // Fixed: Removed redundant "v1/" because it is already in RetrofitClient's BASE_URL
    @GET("edition/format/audio")
    suspend fun getAvailableReciters(): Response<ReciterResponse>
}
