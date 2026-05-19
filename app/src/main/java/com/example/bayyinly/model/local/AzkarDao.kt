package com.example.bayyinly.model.local

import androidx.room.Dao
import androidx.room.Query
import com.example.bayyinly.model.entity.AzkarEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface AzkarDao {
    @Query("SELECT * FROM azkar WHERE category = :category ORDER BY id ASC")
    fun getEntriesByCategory(category: String): Flow<List<AzkarEntry>>

    @Query("SELECT * FROM azkar WHERE category = :category ORDER BY id ASC")
    suspend fun getEntriesByCategorySync(category: String): List<AzkarEntry>

    @Query("SELECT COUNT(*) FROM azkar WHERE category = :category")
    suspend fun getCountByCategory(category: String): Int

    @Query("SELECT * FROM azkar ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomEntry(): List<AzkarEntry>
}
