package com.example.bayyinly.ui.quran

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.bayyinly.database.QuranDatabase
import com.example.bayyinly.databinding.FragmentReadingBinding
import com.example.bayyinly.model.SurahData
import com.example.bayyinly.network.RetrofitClient
import com.example.bayyinly.repository.QuranRepository
import com.example.bayyinly.repository.UserStatsRepository
import com.example.bayyinly.viewmodel.QuranViewModel
import com.example.bayyinly.viewmodel.QuranViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ReadingFragment : Fragment() {

    private var _binding: FragmentReadingBinding? = null
    private val binding get() = _binding!!

    private var mediaPlayer: MediaPlayer? = null
    private var isPlayerReady = false

    private lateinit var viewModel: QuranViewModel
    private lateinit var adapter: AyahAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Get the Surah ID & Setup Adapter
        val surahId = arguments?.getInt("surahId") ?: 1
        adapter = AyahAdapter { toggleAudioPanel() }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewAyahs.layoutManager = layoutManager
        binding.recyclerViewAyahs.adapter = adapter

        // 2. Set the Header Title
        val currentSurah = SurahData.surahs.find { it.id == surahId }
        val englishName = currentSurah?.englishName ?: "Unknown"
        val arabicName = currentSurah?.arabicName ?: "Unknown"
        binding.tvSurahNameTitle.text = "$surahId. $englishName ($arabicName)"

        // 3. Initialize Repositories and ViewModel (UPDATED FOR KHATMA TRACKING)
        val database = QuranDatabase.getDatabase(requireContext())
        val quranDao = database.quranDao()
        val statsDao = database.userStatsDao()

        val quranRepository = QuranRepository(quranDao, RetrofitClient.apiService)
        val statsRepository = UserStatsRepository(statsDao)

        val factory = QuranViewModelFactory(quranRepository, statsRepository)
        viewModel = ViewModelProvider(this, factory)[QuranViewModel::class.java]

        // --- NEW: MANUAL READING TRACKER ---
        binding.recyclerViewAyahs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Find all currently visible items on the screen
                val firstVisible = layoutManager.findFirstVisibleItemPosition()
                val lastVisible = layoutManager.findLastVisibleItemPosition()

                if (firstVisible != RecyclerView.NO_POSITION && lastVisible != RecyclerView.NO_POSITION) {
                    val currentList = adapter.currentList
                    // Loop through the visible items and tell the ViewModel they were "read"
                    for (i in firstVisible..lastVisible) {
                        if (i in currentList.indices) {
                            val ayah = currentList[i]
                            viewModel.markAyahAsRead(ayah.id)
                        }
                    }
                }
            }
        })

        // --- 4. OBSERVE DATA STATES ---

        // A. Observe Database for Verses
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getSurahVerses(surahId).collectLatest { verses ->
                adapter.submitList(verses)
                binding.tvAyahCount.text = "${verses.size} Ayahs"

                viewModel.setPlaylist(verses.map { it.id })

                if (verses.isNotEmpty()) {
                    val ayahNumbers = verses.map { it.ayah.toString() }
                    val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ayahNumbers)
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    binding.spinnerStartAyah.adapter = spinnerAdapter
                    binding.spinnerEndAyah.adapter = spinnerAdapter

                    // Set initial values silently
                    binding.spinnerStartAyah.setSelection(0, false)
                    binding.spinnerEndAyah.setSelection(verses.size - 1, false)

                    // FIX: DETECT REAL-TIME DROPDOWN CHANGES
                    val rangeChangeListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            // Only auto-restart if the audio engine has already been activated!
                            if (viewModel.currentAudioUrl.value != null) {
                                val startIndex = binding.spinnerStartAyah.selectedItemPosition
                                var endIndex = binding.spinnerEndAyah.selectedItemPosition

                                // Safety guard: If user sets Start Ayah AFTER End Ayah, auto-fix the End Ayah to match
                                if (startIndex > endIndex) {
                                    endIndex = startIndex
                                    binding.spinnerEndAyah.setSelection(endIndex)
                                }

                                if (startIndex != -1 && endIndex != -1) {
                                    viewModel.startAudioSequence(startIndex, endIndex)
                                }
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }

                    // Attach the active listeners
                    binding.spinnerStartAyah.onItemSelectedListener = rangeChangeListener
                    binding.spinnerEndAyah.onItemSelectedListener = rangeChangeListener
                }
            }
        }

        // B. Observe Network for Reciters (Sheikhs)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recitersList.collectLatest { reciters ->
                if (reciters.isNotEmpty()) {
                    val reciterNames = reciters.map { it.englishName }
                    val reciterAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, reciterNames)
                    reciterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerReciter.adapter = reciterAdapter

                    binding.spinnerReciter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val selectedReciter = reciters[position]
                            viewModel.setReciter(selectedReciter.identifier)
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                }
            }
        }

        // --- 5. OBSERVE VIEWMODEL AUDIO STATE ---

        // Observe the Active Verse for Highlighting & Auto-Scrolling
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.activeVerseId.collectLatest { activeId ->
                // 1. Tell the adapter to highlight the verse
                adapter.setActiveVerse(activeId)

                // 2. Premium Auto-Scroll: Bring the active Ayah near the top of the screen!
                if (activeId != null) {
                    val position = adapter.currentList.indexOfFirst { it.id == activeId }
                    if (position != -1) {
                        // Create a custom scroller that snaps the item to the START (top) of the screen
                        val smoothScroller = object : LinearSmoothScroller(requireContext()) {
                            override fun getVerticalSnapPreference(): Int {
                                return SNAP_TO_START
                            }
                        }
                        smoothScroller.targetPosition = position
                        binding.recyclerViewAyahs.layoutManager?.startSmoothScroll(smoothScroller)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentAudioUrl.collectLatest { url ->
                if (url != null) {
                    playUrl(url)
                } else {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                    // Clear the highlight when playback finishes completely
                    adapter.setActiveVerse(null)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isPlaying.collectLatest { isPlaying ->
                if (isPlaying) {
                    binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
                    if (isPlayerReady) mediaPlayer?.start()
                } else {
                    binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
                    if (isPlayerReady) mediaPlayer?.pause()
                }
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // --- 6. AUDIO CONTROLS ---
        binding.btnPlayPause.setOnClickListener {
            val startIndex = binding.spinnerStartAyah.selectedItemPosition
            val endIndex = binding.spinnerEndAyah.selectedItemPosition

            if (startIndex == -1 || endIndex == -1) return@setOnClickListener

            if (mediaPlayer == null || viewModel.currentAudioUrl.value == null) {
                viewModel.startAudioSequence(startIndex, endIndex)
            } else {
                viewModel.togglePlayPause()
            }
        }

        binding.btnNextAyah.setOnClickListener {
            if (viewModel.currentAudioUrl.value != null) {
                viewModel.playNextAyah()
            }
        }

        binding.btnPreviousAyah.setOnClickListener {
            if (viewModel.currentAudioUrl.value != null) {
                viewModel.playPreviousAyah()
            }
        }
    }

    private fun toggleAudioPanel() {
        binding.audioControlPanel.visibility = if (binding.audioControlPanel.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun playUrl(url: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
        isPlayerReady = false

        try {
            mediaPlayer?.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )

            Log.d("QuranAudio", "Attempting to stream: $url")

            mediaPlayer?.apply {
                setDataSource(url)
                prepareAsync()

                setOnPreparedListener {
                    isPlayerReady = true
                    if (viewModel.isPlaying.value) {
                        start()
                    }
                }

                setOnCompletionListener {
                    viewModel.onAyahAudioFinished()
                }

                setOnErrorListener { _, _, _ ->
                    isPlayerReady = false
                    if (viewModel.isPlaying.value) {
                        viewModel.togglePlayPause()
                    }
                    Toast.makeText(requireContext(), "This Reciter does not have audio for this Ayah.", Toast.LENGTH_LONG).show()
                    true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error configuring audio player", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }
}