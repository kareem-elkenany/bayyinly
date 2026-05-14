package com.example.bayyinly.model.local

import androidx.room.Dao
import androidx.room.Query
import com.example.bayyinly.model.CombinedVerse
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao {
    @Query("SELECT q.id, q.surah, q.ayah, q.text AS arabicText, t.text AS englishText FROM quran_text q INNER JOIN translation_text t ON q.surah = t.surah AND q.ayah = t.ayah WHERE q.surah = :surahId ORDER BY q.ayah ASC")
    fun getSurahWithTranslation(surahId: Int): Flow<List<CombinedVerse>>
}