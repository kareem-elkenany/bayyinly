package com.example.bayyinly.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 0,
    val khatmaCompletedAyahs: Int = 0, // Current count (0 to 6236)
    val quranStreak: Int = 0,
    val lastReadDate: String = ""
)