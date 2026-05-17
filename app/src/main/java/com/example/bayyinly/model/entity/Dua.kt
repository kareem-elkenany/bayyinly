package com.example.bayyinly.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "duas")
data class Dua(
    @PrimaryKey val id: Int,
    val category: String,
    val title: String,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val source: String
)
