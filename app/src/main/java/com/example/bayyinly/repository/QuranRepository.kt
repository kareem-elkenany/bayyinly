package com.example.bayyinly.repository

import com.example.bayyinly.model.CombinedVerse
import com.example.bayyinly.model.local.QuranDao
import kotlinx.coroutines.flow.Flow

// The Repository takes the DAO in its constructor
class QuranRepository(private val quranDao: QuranDao) {

    // 1. Pass the read query straight through as a Flow
    fun getSurahWithTranslation(surahId: Int): Flow<List<CombinedVerse>> {
        return quranDao.getSurahWithTranslation(surahId)
    }
}