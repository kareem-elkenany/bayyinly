package com.example.bayyinly.model

import com.google.gson.annotations.SerializedName

// The main response wrapper from the API
data class ReciterResponse(
    val code: Int,
    val status: String,
    val data: List<Reciter>
)

// The actual Sheikh data
data class Reciter(
    val identifier: String, // e.g., "ar.alafasy"
    val language: String,
    val name: String,
    val englishName: String,
    val format: String,
    val type: String
)