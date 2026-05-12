package com.example.bayyinly.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quran_text")
data class QuranText(
    @PrimaryKey val id: Int?,
    val surah: Int?,
    val ayah: Int?,
    val text: String?
)