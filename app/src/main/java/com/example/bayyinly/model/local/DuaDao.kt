package com.example.bayyinly.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bayyinly.model.entity.Dua
import kotlinx.coroutines.flow.Flow

@Dao
interface DuaDao {
    @Query("SELECT * FROM duas ORDER BY id ASC")
    fun getAllDuas(): Flow<List<Dua>>

    @Query("SELECT COUNT(*) FROM duas")
    fun countDuas(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(duas: List<Dua>): List<Long>
}
