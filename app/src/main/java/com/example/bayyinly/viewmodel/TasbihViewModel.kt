package com.example.bayyinly.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bayyinly.database.AzkarDatabase
import com.example.bayyinly.model.entity.AzkarEntry
import com.example.bayyinly.repository.AzkarRepository
import kotlinx.coroutines.launch

class TasbihViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AzkarRepository(AzkarDatabase.getDatabase(app).azkarDao())

    private val tasbihList = listOf(
        AzkarEntry(
            id = -1,
            category = "Tasbih",
            zekr = "سُبْحَانَ اللهِ",
            description = "Glory be to Allah",
            count = 33,
            reference = null,
            search = "subhanallah"
        ),
        AzkarEntry(
            id = -2,
            category = "Tasbih",
            zekr = "الْحَمْدُ للهِ",
            description = "Praise be to Allah",
            count = 33,
            reference = null,
            search = "alhamdulillah"
        ),
        AzkarEntry(
            id = -3,
            category = "Tasbih",
            zekr = "اللهُ أَكْبَرُ",
            description = "Allah is the Greatest",
            count = 34,
            reference = null,
            search = "allahu akbar"
        )
    )

    private var azkarIndex = 0

    private val _currentAzkar = MutableLiveData<AzkarEntry?>()
    val currentAzkar: LiveData<AzkarEntry?> = _currentAzkar

    private val _count = MutableLiveData(0)
    val count: LiveData<Int> = _count

    private val _totalCount = MutableLiveData(0)
    val totalCount: LiveData<Int> = _totalCount

    private val _target = MutableLiveData(33)
    val target: LiveData<Int> = _target

    init {
        _currentAzkar.value = tasbihList[0]
        _target.value = tasbihList[0].count ?: 33
    }

    fun increment() {
        val next = (_count.value ?: 0) + 1
        _totalCount.value = (_totalCount.value ?: 0) + 1
        val currentTarget = _target.value ?: 33
        if (next >= currentTarget) {
            advanceToNextAzkar()
        } else {
            _count.value = next
        }
    }

    fun nextAzkar() {
        advanceToNextAzkar()
    }

    fun reset() {
        _count.value = 0
        _totalCount.value = 0
        azkarIndex = 0
        _currentAzkar.value = tasbihList[0]
        _target.value = tasbihList[0].count ?: 33
    }

    private fun advanceToNextAzkar() {
        azkarIndex = (azkarIndex + 1) % tasbihList.size
        val azkar = tasbihList[azkarIndex]
        _currentAzkar.value = azkar
        _target.value = azkar.count ?: 33
        _count.value = 0
    }
}
