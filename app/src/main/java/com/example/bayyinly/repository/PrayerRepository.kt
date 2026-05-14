package com.example.bayyinly.repository

import com.example.bayyinly.model.PrayerResponse
import com.example.bayyinly.network.PrayerApiService
import retrofit2.Response

class PrayerRepository(private val apiService: PrayerApiService) {

    /**
     * Fetches prayer times using city and country names.
     * Ideal for users who prefer to set their location manually.
     */
    suspend fun getPrayerTimesByCity(
        city: String,
        country: String,
        method: Int? = null
    ): Response<PrayerResponse> {
        return apiService.getPrayerTimesByCity(city, country, method)
    }

    /**
     * Fetches prayer times using GPS coordinates.
     * This ensures worldwide accuracy regardless of city names.
     */
    suspend fun getPrayerTimesByCoords(
        lat: Double,
        lng: Double,
        method: Int? = null
    ): Response<PrayerResponse> {
        return apiService.getPrayerTimesByCoords(lat, lng, method)
    }
}