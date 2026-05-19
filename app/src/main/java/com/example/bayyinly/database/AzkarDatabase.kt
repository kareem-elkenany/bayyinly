package com.example.bayyinly.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bayyinly.model.entity.AzkarEntry
import com.example.bayyinly.model.local.AzkarDao

@Database(entities = [AzkarEntry::class], version = 1, exportSchema = false)
abstract class AzkarDatabase : RoomDatabase() {

    abstract fun azkarDao(): AzkarDao

    companion object {
        @Volatile
        private var INSTANCE: AzkarDatabase? = null

        fun getDatabase(context: Context): AzkarDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AzkarDatabase::class.java,
                    "azkar_database"
                )
                    .createFromAsset("azkar.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
