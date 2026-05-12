package com.example.bayyinly.ui.quran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bayyinly.databinding.FragmentReadingBinding
import com.example.bayyinly.database.QuranDatabase // Make sure this matches your DB class!
import com.example.bayyinly.repository.QuranRepository
import com.example.bayyinly.viewmodel.QuranViewModel
import com.example.bayyinly.viewmodel.QuranViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.widget.Toast
import com.example.bayyinly.model.SurahData

class ReadingFragment : Fragment() {

    private var _binding: FragmentReadingBinding? = null
    private val binding get() = _binding!!

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

        // 1. Get the Surah ID
        val surahId = arguments?.getInt("surahId") ?: 1

        // 2. Setup the RecyclerView and Adapter
        adapter = AyahAdapter()
        binding.recyclerViewAyahs.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewAyahs.adapter = adapter

        // --- NEW CODE: Set the Title immediately ---
        // Find the Surah info from your static list (Make sure your variable names match your SurahData class!)
        val currentSurah = SurahData.surahs.find { it.id == surahId }
        val englishName = currentSurah?.englishName ?: "Unknown" // Change if your property is named differently
        val arabicName = currentSurah?.arabicName ?: "Unknown"   // Change if your property is named differently

        // Format: "1. Al-Fatiha (الفاتحة)"
        binding.tvSurahNameTitle.text = "$surahId. $englishName ($arabicName)"
        // -----------------------------------------

        // 3. Initialize the ViewModel
        val dao = QuranDatabase.getDatabase(requireContext()).quranDao()
        val repository = QuranRepository(dao)
        val factory = QuranViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[QuranViewModel::class.java]

        // 4. Observe the Database Flow
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getSurahVerses(surahId).collectLatest { verses ->
                adapter.submitList(verses)

                // Update the smaller sub-title with the exact count from the database
                binding.tvAyahCount.text = "${verses.size} Ayahs"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}