package com.example.bayyinly.model

// Notice there are no Room annotations here!
// That's because combined verse isn't in our database
data class CombinedVerse(
    val id: Int,
    val surah: Int,
    val ayah: Int,
    val arabicText: String,
    val englishText: String
)