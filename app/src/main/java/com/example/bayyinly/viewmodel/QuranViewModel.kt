package com.example.bayyinly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bayyinly.model.CombinedVerse
import com.example.bayyinly.repository.QuranRepository
import kotlinx.coroutines.flow.Flow

class QuranViewModel(private val repository: QuranRepository) : ViewModel() {

    // The Fragment will call this function to get the stream of verses
    fun getSurahVerses(surahId: Int): Flow<List<CombinedVerse>> {
        return repository.getSurahWithTranslation(surahId)
    }
}

// ----------------------------------------------------------------------
// FACTORY: Because our ViewModel needs a Repository passed into it,
// we have to build a "Factory" to tell Android how to create the ViewModel.
// ----------------------------------------------------------------------
class QuranViewModelFactory(private val repository: QuranRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuranViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuranViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}