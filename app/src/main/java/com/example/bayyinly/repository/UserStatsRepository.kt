package com.example.bayyinly.repository

import com.example.bayyinly.model.local.UserStatsDao
import com.example.bayyinly.model.entity.UserStats
import com.example.bayyinly.model.ReadAyah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class UserStatsRepository(private val userStatsDao: UserStatsDao) {

    private val TOTAL_QURAN_AYAHS = 6236.0

    // Missing function #1
    fun getKhatmaPercentage(): Flow<Int> = userStatsDao.getReadAyahsCount().map { count ->
        val percentage = (count / TOTAL_QURAN_AYAHS) * 100
        percentage.toInt().coerceAtMost(100)
    }

    // Missing function #2
    fun getUserStats(): Flow<UserStats?> = userStatsDao.getUserStats()

    suspend fun markAyahAsRead(id: Int) {
        withContext(Dispatchers.IO) {
            userStatsDao.markAyahAsRead(ReadAyah(id))
            handleStreakUpdate()
        }
    }

    private suspend fun handleStreakUpdate() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val today = sdf.format(Date())

        // Get snapshot from Flow
        val currentStats = userStatsDao.getUserStats().firstOrNull()
            ?: UserStats(id = 0, quranStreak = 0, lastReadDate = "")

        if (currentStats.lastReadDate == today) return

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = sdf.format(calendar.time)

        val newStreak = if (currentStats.lastReadDate == yesterday) currentStats.quranStreak + 1 else 1

        val updatedStats = currentStats.copy(
            quranStreak = newStreak,
            lastReadDate = today
        )

        // DAO is synchronous, but we are in Dispatchers.IO
        userStatsDao.upsertStats(updatedStats)
    }

    suspend fun resetAllProgress() {
        withContext(Dispatchers.IO) {
            userStatsDao.clearAllReadAyahs()
            userStatsDao.resetGeneralStats()
        }
    }
}