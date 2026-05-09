package com.example.bayyinly.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bayyinly.model.entity.QuranText
import com.example.bayyinly.model.entity.TranslationText
import com.example.bayyinly.model.local.QuranDao


// 1. Tell Room which Entities belong to this database
@Database(entities = [QuranText::class, TranslationText::class], version = 1, exportSchema = false)
abstract class QuranDatabase : RoomDatabase() {

    // 2. Connect the DAO
    abstract fun quranDao(): QuranDao

    // 3. Create a Singleton to prevent opening multiple database instances at once
    companion object {
        @Volatile //Thread safety
        private var INSTANCE: QuranDatabase? = null

        fun getDatabase(context: Context): QuranDatabase {
            // If the instance exists, return it. Otherwise, build it.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuranDatabase::class.java,
                    "quran_database" // The internal name Android uses
                )
                    // THIS IS THE MAGIC LINE:
                    // It copies your pre-made file from the assets folder!
                    .createFromAsset("quran_offline.db")
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}