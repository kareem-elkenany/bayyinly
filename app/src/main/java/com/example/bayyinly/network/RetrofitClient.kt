package com.example.bayyinly.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Keep your original URL
    private const val QURAN_BASE_URL = "https://api.alquran.cloud/v1/"
    // Add the new Prayer URL
    private const val PRAYER_BASE_URL = "https://api.aladhan.com/v1/"

    // --- Quran Setup (DO NOT TOUCH - Keeps your existing code working) ---
    private val quranRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(QURAN_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: QuranApiService by lazy {
        quranRetrofit.create(QuranApiService::class.java)
    }

    // --- Prayer Setup (NEW - For your HomeFragment) ---
    private val prayerRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(PRAYER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val prayerApiService: PrayerApiService by lazy {
        prayerRetrofit.create(PrayerApiService::class.java)
    }
}