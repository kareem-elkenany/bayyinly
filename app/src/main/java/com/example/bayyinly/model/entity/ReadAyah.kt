package com.example.bayyinly.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "read_ayahs")
data class ReadAyah(
    @PrimaryKey val ayahId: Int // The absolute ID (1 to 6236)
)