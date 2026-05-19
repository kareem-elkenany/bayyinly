package com.example.bayyinly.repository

import com.example.bayyinly.model.local.AzkarDao

class AzkarRepository(private val dao: AzkarDao) {

    companion object {
        const val MORNING     = "أذكار الصباح"
        const val EVENING     = "أذكار المساء"
        const val AFTER_PRAYER = "الأذكار بعد السلام من الصلاة"
        const val SLEEP       = "أذكار النوم"
        const val GENERAL     = "التسبيح، التحميد، التهليل، التكبير"
    }

    fun getEntriesByCategory(category: String) = dao.getEntriesByCategory(category)

    suspend fun getEntriesByCategorySync(category: String) = dao.getEntriesByCategorySync(category)

    suspend fun getCountByCategory(category: String) = dao.getCountByCategory(category)

    suspend fun getRandomEntry() = dao.getRandomEntry().firstOrNull()
}
