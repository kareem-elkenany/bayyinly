package com.example.bayyinly.network

import com.example.bayyinly.model.PrayerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PrayerApiService {

    // Method 1: Coordinates (Best for Worldwide/GPS)
    @GET("timings")
    suspend fun getPrayerTimesByCoords(
        @Query("latitude") lat: Double,
        @Query("longitude") lng: Double,
        @Query("method") method: Int? = null // API will auto-detect if null
    ): Response<PrayerResponse>

    // Method 2: City/Country (Good for Manual Search)
    @GET("timingsByCity")
    suspend fun getPrayerTimesByCity(
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") method: Int? = null
    ): Response<PrayerResponse>
}