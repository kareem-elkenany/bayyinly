package com.example.bayyinly.model

import com.google.gson.annotations.SerializedName

data class PrayerResponse(
    val code: Int,
    val status: String,
    val data: PrayerData
)

data class PrayerData(
    val timings: Timings,
    val date: PrayerDate,
    val meta: PrayerMeta
)

data class Timings(
    @SerializedName("Fajr") val fajr: String,
    @SerializedName("Dhuhr") val dhuhr: String,
    @SerializedName("Asr") val asr: String,
    @SerializedName("Maghrib") val maghrib: String,
    @SerializedName("Isha") val isha: String
)

data class PrayerDate(
    val readable: String,
    val timestamp: String
)

data class PrayerMeta(
    val timezone: String,
    val method: CalculationMethod
)

data class CalculationMethod(
    val id: Int,
    val name: String
)