package com.example.bayyinly.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "azkar")
data class AzkarEntry(
    @PrimaryKey val id: Int,
    val category: String,
    val zekr: String,
    val description: String?,
    val count: Int?,
    val reference: String?,
    val search: String?
)
