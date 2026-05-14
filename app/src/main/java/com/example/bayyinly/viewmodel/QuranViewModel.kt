package com.example.bayyinly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bayyinly.model.CombinedVerse
import com.example.bayyinly.model.Reciter
import com.example.bayyinly.repository.QuranRepository
import com.example.bayyinly.repository.UserStatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class QuranViewModel(
    private val repository: QuranRepository,
    private val statsRepository: UserStatsRepository
) : ViewModel() {

    // Unique session tracking to avoid redundant DB calls while scrolling or repeating audio
    private val readAyahIdsInSession = mutableSetOf<Int>()

    fun getSurahVerses(surahId: Int): Flow<List<CombinedVerse>> {
        return repository.getSurahWithTranslation(surahId)
    }

    private val _recitersList = MutableStateFlow<List<Reciter>>(emptyList())
    val recitersList = _recitersList.asStateFlow()

    private val _currentAudioUrl = MutableStateFlow<String?>(null)
    val currentAudioUrl = _currentAudioUrl.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

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
        _activeVerseId.value = absoluteAyahId
    }

    /**
     * Logic updated to use the new unique tracking system.
     */
    fun onAyahAudioFinished() {
        // Track the Ayah that just finished playing
        markAyahAsRead(currentPlaylist[currentPlayIndex])

        if (currentPlayIndex < targetEndIndex && currentPlayIndex < currentPlaylist.size - 1) {
            currentPlayIndex++
            val nextAbsoluteId = currentPlaylist[currentPlayIndex]
            _currentAudioUrl.value = repository.getAudioStreamUrl(selectedReciterIdentifier, nextAbsoluteId)
            _activeVerseId.value = nextAbsoluteId
        } else {
            _currentAudioUrl.value = null
            _isPlaying.value = false
            _activeVerseId.value = null
        }
    }

    /**
     * Public function for both Audio logic and Manual Scroll tracking.
     */
    fun markAyahAsRead(ayahId: Int) {
        if (!readAyahIdsInSession.contains(ayahId)) {
            viewModelScope.launch {
                statsRepository.markAyahAsRead(ayahId) // Uses OnConflictStrategy.IGNORE internally
                readAyahIdsInSession.add(ayahId)
            }
        }
    }

    /**
     * Resets all progress. Call this from a "Settings" or "Reset" button.
     */
    fun resetKhatmaProgress() {
        viewModelScope.launch {
            statsRepository.resetAllProgress()
            readAyahIdsInSession.clear() // Clear in-memory cache too
        }
    }

    fun playNextAyah() {
        if (currentPlayIndex < targetEndIndex && currentPlayIndex < currentPlaylist.size - 1) {
            // Optional: Mark as read even if they skip, if they listened to most of it
            markAyahAsRead(currentPlaylist[currentPlayIndex])

            currentPlayIndex++
            val nextAbsoluteId = currentPlaylist[currentPlayIndex]
            _currentAudioUrl.value = repository.getAudioStreamUrl(selectedReciterIdentifier, nextAbsoluteId)
            _isPlaying.value = true
            _activeVerseId.value = nextAbsoluteId
        }
    }

    fun playPreviousAyah() {
        if (currentPlayIndex > 0) {
            currentPlayIndex--
            val prevAbsoluteId = currentPlaylist[currentPlayIndex]
            _currentAudioUrl.value = repository.getAudioStreamUrl(selectedReciterIdentifier, prevAbsoluteId)
            _isPlaying.value = true
            _activeVerseId.value = prevAbsoluteId
        }
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun getCurrentPlayIndex() = currentPlayIndex
}

class QuranViewModelFactory(
    private val repository: QuranRepository,
    private val statsRepository: UserStatsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuranViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuranViewModel(repository, statsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}