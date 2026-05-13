package com.example.bayyinly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bayyinly.model.CombinedVerse
import com.example.bayyinly.model.Reciter
import com.example.bayyinly.repository.QuranRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuranViewModel(private val repository: QuranRepository) : ViewModel() {

    fun getSurahVerses(surahId: Int): Flow<List<CombinedVerse>> {
        return repository.getSurahWithTranslation(surahId)
    }

    private val _recitersList = MutableStateFlow<List<Reciter>>(emptyList())
    val recitersList = _recitersList.asStateFlow()

    private val _currentAudioUrl = MutableStateFlow<String?>(null)
    val currentAudioUrl = _currentAudioUrl.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    // FIX: Explicitly telling Kotlin this is an Integer that can be null (<Int?>)
    private val _activeVerseId = MutableStateFlow<Int?>(null)
    val activeVerseId = _activeVerseId.asStateFlow()

    private var currentPlaylist: List<Int> = emptyList()
    private var currentPlayIndex = 0
    private var targetEndIndex = 0
    private var selectedReciterIdentifier = "ar.alafasy"

    init { fetchReciters() }

    private fun fetchReciters() {
        viewModelScope.launch {
            try {
                val response = repository.getReciters()
                if (response.isSuccessful && response.body() != null) {

                    // The Verified Whitelist (Guaranteed 128kbps Arabic Audio)
                    val reliableIdentifiers = setOf(
                        "ar.alafasy", "ar.abdulbasitmurattal", "ar.sudais",
                        "ar.shuraim", "ar.minshawi", "ar.husary",
                        "ar.mahermuaiqly", "ar.hudhaify", "ar.muhammadjibreel",
                        "ar.aymanswaid", "ar.ahmedajamy"
                    )

                    val safeReciters = response.body()!!.data.filter { reciter ->
                        reciter.language == "ar" &&
                                reciter.type == "versebyverse" &&
                                reliableIdentifiers.contains(reciter.identifier)
                    }

                    _recitersList.value = safeReciters

                    if (safeReciters.isNotEmpty() && selectedReciterIdentifier == "ar.alafasy") {
                        selectedReciterIdentifier = safeReciters[0].identifier
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setPlaylist(verseIds: List<Int>) {
        currentPlaylist = verseIds
    }

    fun setReciter(identifier: String) {
        selectedReciterIdentifier = identifier
        if (_currentAudioUrl.value != null && currentPlaylist.isNotEmpty()) {
            val absoluteAyahId = currentPlaylist[currentPlayIndex]
            _currentAudioUrl.value = repository.getAudioStreamUrl(selectedReciterIdentifier, absoluteAyahId)
        }
    }

    fun startAudioSequence(startIndex: Int, endIndex: Int) {
        if (currentPlaylist.isEmpty() || startIndex >= currentPlaylist.size || startIndex > endIndex) return

        currentPlayIndex = startIndex
        targetEndIndex = endIndex

        val absoluteAyahId = currentPlaylist[currentPlayIndex]
        _currentAudioUrl.value = repository.getAudioStreamUrl(selectedReciterIdentifier, absoluteAyahId)
        _isPlaying.value = true

        // Tell the UI which Ayah to highlight
        _activeVerseId.value = absoluteAyahId
    }

    fun onAyahAudioFinished() {
        if (currentPlayIndex < targetEndIndex && currentPlayIndex < currentPlaylist.size - 1) {
            currentPlayIndex++
            val nextAbsoluteId = currentPlaylist[currentPlayIndex]
            _currentAudioUrl.value = repository.getAudioStreamUrl(selectedReciterIdentifier, nextAbsoluteId)

            // Update highlight to the next Ayah
            _activeVerseId.value = nextAbsoluteId
        } else {
            _currentAudioUrl.value = null
            _isPlaying.value = false

            // Remove highlight when sequence finishes
            _activeVerseId.value = null
        }
    }

    fun playNextAyah() {
        if (currentPlayIndex < targetEndIndex && currentPlayIndex < currentPlaylist.size - 1) {
            currentPlayIndex++
            val nextAbsoluteId = currentPlaylist[currentPlayIndex]
            _currentAudioUrl.value = repository.getAudioStreamUrl(selectedReciterIdentifier, nextAbsoluteId)
            _isPlaying.value = true

            // Update highlight
            _activeVerseId.value = nextAbsoluteId
        }
    }

    fun playPreviousAyah() {
        if (currentPlayIndex > 0) {
            currentPlayIndex--
            val prevAbsoluteId = currentPlaylist[currentPlayIndex]
            _currentAudioUrl.value = repository.getAudioStreamUrl(selectedReciterIdentifier, prevAbsoluteId)
            _isPlaying.value = true

            // Update highlight
            _activeVerseId.value = prevAbsoluteId
        }
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun getCurrentPlayIndex() = currentPlayIndex
}

class QuranViewModelFactory(private val repository: QuranRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuranViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuranViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}