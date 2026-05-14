package com.example.bayyinly.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.bayyinly.model.ReadAyah
import com.example.bayyinly.model.entity.UserStats
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun markAyahAsRead(ayah: ReadAyah): Long

    @Upsert
    fun upsertStats(stats: UserStats)

    @Query("SELECT COUNT(*) FROM read_ayahs")
    fun getReadAyahsCount(): Flow<Int>

    @Query("SELECT * FROM user_stats WHERE id = 0 LIMIT 1")
    fun getUserStats(): Flow<UserStats?>

    @Query("DELETE FROM read_ayahs")
    fun clearAllReadAyahs(): Int

    @Query("UPDATE user_stats SET quranStreak = 0, lastReadDate = '' WHERE id = 0")
    fun resetGeneralStats(): Int
}