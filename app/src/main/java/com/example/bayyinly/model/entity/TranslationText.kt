package com.example.bayyinly.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translation_text")
data class TranslationText(
    @PrimaryKey val id: Int,
    val surah: Int,
    val ayah: Int,
    val text: String
)