package com.example.bayyinly.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bayyinly.model.entity.Dua
import com.example.bayyinly.model.local.DuaDao

@Database(
    entities = [Dua::class],
    version = 4,
    exportSchema = false
)
abstract class DuaDatabase : RoomDatabase() {

    abstract fun duaDao(): DuaDao

    companion object {
        @Volatile
        private var INSTANCE: DuaDatabase? = null

        fun getDatabase(context: Context): DuaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DuaDatabase::class.java,
                    "dua_database"
                )
                    .createFromAsset("dua_offline.db")
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
