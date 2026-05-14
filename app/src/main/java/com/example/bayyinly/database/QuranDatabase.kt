package com.example.bayyinly.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bayyinly.model.entity.QuranText
import com.example.bayyinly.model.entity.TranslationText
import com.example.bayyinly.model.entity.UserStats
import com.example.bayyinly.model.ReadAyah
import com.example.bayyinly.model.local.QuranDao
import com.example.bayyinly.model.local.UserStatsDao

@Database(
    entities = [QuranText::class, TranslationText::class, UserStats::class, ReadAyah::class],
    version = 3,
    exportSchema = false
)
abstract class QuranDatabase : RoomDatabase() {

    abstract fun quranDao(): QuranDao
    abstract fun userStatsDao(): UserStatsDao

    companion object {
        @Volatile
        private var INSTANCE: QuranDatabase? = null

        fun getDatabase(context: Context): QuranDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuranDatabase::class.java,
                    "quran_database"
                )
                    .createFromAsset("quran_offline.db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}