package com.example.bayyinly.ui.quran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bayyinly.R
import com.example.bayyinly.databinding.FragmentSurahListBinding
import com.example.bayyinly.model.SurahData
import java.util.Locale

class SurahListFragment : Fragment() {

    private var _binding: FragmentSurahListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSurahListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewSurahs.layoutManager = LinearLayoutManager(requireContext())

        val adapter = SurahAdapter(SurahData.surahs) { clickedSurahId ->
            val bundle = bundleOf("surahId" to clickedSurahId)
            findNavController().navigate(R.id.action_nav_quran_to_readingFragment, bundle)
        }
        binding.recyclerViewSurahs.adapter = adapter

        binding.etSearch.addTextChangedListener { text ->
            val query = text.toString().trim()

            // Normalize English search terms
            val normalizedEnglishQuery = query.lowercase(Locale.ROOT).normalizeForSearch()
            val queryWords = normalizedEnglishQuery.split(" ").filter { it.isNotEmpty() }

            // Normalize Arabic search terms (Remove Tashkeel/Diacritics)
            val normalizedArabicQuery = query.removeArabicTashkeel()

            val filteredList = if (query.isEmpty()) {
                SurahData.surahs
            } else {
                SurahData.surahs.filter { surah ->
                    // 1. English Matching
                    val normalizedEnglishName = surah.englishName.lowercase(Locale.ROOT).normalizeForSearch()
                    val matchesEnglish = queryWords.isNotEmpty() && queryWords.all { word ->
                        normalizedEnglishName.contains(word)
                    }

                    // 2. Arabic Matching (Comparing clean text vs clean text)
                    val cleanArabicName = surah.arabicName.removeArabicTashkeel()
                    val matchesArabic = cleanArabicName.contains(normalizedArabicQuery)

                    matchesEnglish || matchesArabic
                }
            }

            adapter.updateList(filteredList)
        }
    }

    /**
     * Cleans English strings by removing hyphens and apostrophes.
     * Note: Removed the aggressive regex that was killing Arabic characters.
     */
    private fun String.normalizeForSearch(): String {
        return this.replace("-", " ")
            .replace("'", "")
            .trim()
    }

    /**
     * Removes Arabic diacritics (Fatha, Damma, Kasra, Tanwin, etc.)
     * This ensures "الفاتحة" matches "ٱلْفَاتِحَةِ".
     */
    private fun String.removeArabicTashkeel(): String {
        val tashkeelRegex = Regex("[\\u0617-\\u061A\\u064B-\\u0652\\u06D6-\\u06DC\\u06DF-\\u06E8\\u06EA-\\u06ED]")
        return this.replace(tashkeelRegex, "")
            .replace("أ", "ا")
            .replace("إ", "ا")
            .replace("آ", "ا")
            .replace("ة", "ه")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}