package com.example.bayyinly.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bayyinly.model.Azkar
import com.example.bayyinly.model.AzkarData

class TasbihViewModel : ViewModel() {

    private var azkarIndex = 0

    private val _currentAzkar = MutableLiveData<Azkar>(AzkarData.tasbihList[0])
    val currentAzkar: LiveData<Azkar> = _currentAzkar

    private val _count = MutableLiveData(0)
    val count: LiveData<Int> = _count

    private val _totalCount = MutableLiveData(0)
    val totalCount: LiveData<Int> = _totalCount

    private val _target = MutableLiveData(AzkarData.tasbihList[0].count)
    val target: LiveData<Int> = _target

    fun increment() {
        val next = (_count.value ?: 0) + 1
        _totalCount.value = (_totalCount.value ?: 0) + 1
        if (next >= (_target.value ?: 33)) {
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
    }

    private fun advanceToNextAzkar() {
        azkarIndex = (azkarIndex + 1) % AzkarData.tasbihList.size
        val azkar = AzkarData.tasbihList[azkarIndex]
        _currentAzkar.value = azkar
        _target.value = azkar.count
        _count.value = 0
    }
}
