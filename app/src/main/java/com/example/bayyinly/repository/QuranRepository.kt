package com.example.bayyinly.repository

import com.example.bayyinly.model.CombinedVerse
import com.example.bayyinly.model.local.QuranDao
import kotlinx.coroutines.flow.Flow
import com.example.bayyinly.network.RetrofitClient.apiService
import com.example.bayyinly.network.QuranApiService

// The Repository takes the DAO in its constructor
class QuranRepository(private val quranDao: QuranDao,
                      private val apiService: QuranApiService) {

    // 1. Pass the read query straight through as a Flow
    fun getSurahWithTranslation(surahId: Int): Flow<List<CombinedVerse>> {
        return quranDao.getSurahWithTranslation(surahId)
    }

    // --- NETWORK API (RETROFIT) ---
    suspend fun getReciters() = apiService.getAvailableReciters()

    // The Repository should be the one building the CDN URL, not the ViewModel!
    fun getAudioStreamUrl(reciterIdentifier: String, absoluteAyahId: Int): String {
        return "https://cdn.islamic.network/quran/audio/128/$reciterIdentifier/$absoluteAyahId.mp3"
    }

}