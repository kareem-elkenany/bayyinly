package com.example.bayyinly.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bayyinly.model.Timings
import com.example.bayyinly.repository.PrayerRepository
import com.example.bayyinly.repository.UserStatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(
    private val prayerRepository: PrayerRepository,
    private val statsRepository: UserStatsRepository
) : ViewModel() {

    // 1. UI States for Prayer Times
    private val _prayerTimings = MutableStateFlow<Timings?>(null)
    val prayerTimings = _prayerTimings.asStateFlow()

    private val _nextPrayerName = MutableStateFlow("---")
    val nextPrayerName = _nextPrayerName.asStateFlow()

    private val _nextPrayerTime = MutableStateFlow("--:--")
    val nextPrayerTime = _nextPrayerTime.asStateFlow()

    private val _countdownText = MutableStateFlow("00:00:00")
    val countdownText = _countdownText.asStateFlow()

    // 2. UI States for User Progress (Replacing Hasanat with Khatma)
    private val _khatmaProgress = MutableStateFlow(0) // Percentage 0-100
    val khatmaProgress = _khatmaProgress.asStateFlow()

    private val _streak = MutableStateFlow(0)
    val streak = _streak.asStateFlow()

    private var timer: CountDownTimer? = null

    init {
        loadHomeData()
    }

    /**
     * Orchestrates the initial data load for the Home screen.
     */
    fun loadHomeData() {
        // Placeholder for Cairo, Egypt. You can replace this with actual GPS coords later.
        fetchPrayerTimesByCoords(30.0444, 31.2357)
        fetchUserStats()
    }

    private fun fetchPrayerTimesByCoords(lat: Double, lng: Double) {
        viewModelScope.launch {
            try {
                val response = prayerRepository.getPrayerTimesByCoords(lat, lng)
                if (response.isSuccessful && response.body() != null) {
                    val timings = response.body()!!.data.timings
                    _prayerTimings.value = timings
                    processNextPrayer(timings)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Observes the Khatma percentage and Streak from the repository.
     */
    private fun fetchUserStats() {
        // Observe the Khatma Percentage Flow
        viewModelScope.launch {
            statsRepository.getKhatmaPercentage().collect { progress ->
                _khatmaProgress.value = progress
            }
        }

        // Observe the raw UserStats Flow for the Streak count
        viewModelScope.launch {
            statsRepository.getUserStats().collect { stats ->
                _streak.value = stats?.quranStreak ?: 0
            }
        }
    }

    /**
     * Logic to determine which prayer is next in the sequence.
     */
    private fun processNextPrayer(timings: Timings) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val now = Calendar.getInstance()
        val currentTimeString = sdf.format(now.time)
        val currentTime = sdf.parse(currentTimeString)

        val prayerList = listOf(
            "Fajr" to timings.fajr,
            "Dhuhr" to timings.dhuhr,
            "Asr" to timings.asr,
            "Maghrib" to timings.maghrib,
            "Isha" to timings.isha
        )

        var nextName = "Fajr"
        var nextTimeStr = timings.fajr
        var isTomorrow = true

        for ((name, time) in prayerList) {
            val prayerTime = sdf.parse(time.split(" ")[0])
            if (prayerTime != null && prayerTime.after(currentTime)) {
                nextName = name
                nextTimeStr = time
                isTomorrow = false
                break
            }
        }

        _nextPrayerName.value = nextName
        _nextPrayerTime.value = formatTo12Hour(nextTimeStr)
        startCountdown(nextTimeStr, isTomorrow)
    }

    fun resetProgress() {
        viewModelScope.launch {
            statsRepository.resetAllProgress()
        }
    }

    private fun startCountdown(timeStr: String, isTomorrow: Boolean) {
        timer?.cancel()

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val targetDate = sdf.parse(timeStr.split(" ")[0]) ?: return

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            time = targetDate
            set(Calendar.YEAR, now.get(Calendar.YEAR))
            set(Calendar.MONTH, now.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
            if (isTomorrow) add(Calendar.DAY_OF_MONTH, 1)
        }

        val diff = target.timeInMillis - now.timeInMillis

        timer = object : CountDownTimer(diff, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val h = (millisUntilFinished / 3600000)
                val m = (millisUntilFinished / 60000) % 60
                val s = (millisUntilFinished / 1000) % 60
                _countdownText.value = String.format("%02d:%02d:%02d", h, m, s)
            }

            override fun onFinish() {
                loadHomeData() // Auto-refresh when the timer hits zero
            }
        }.start()
    }

    private fun formatTo12Hour(time24: String): String {
        return try {
            val date = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(time24.split(" ")[0])
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(date!!)
        } catch (e: Exception) { time24 }
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}

class HomeViewModelFactory(
    private val prayerRepo: PrayerRepository,
    private val statsRepo: UserStatsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(prayerRepo, statsRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}