package com.example.bayyinly.ui.dua

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bayyinly.database.DuaDatabase
import com.example.bayyinly.databinding.FragmentDuaCategoryBinding
import com.example.bayyinly.model.entity.Dua
import com.google.android.material.chip.Chip
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class DuaCategoryFragment : Fragment() {
    private var _binding: FragmentDuaCategoryBinding? = null
    private val binding get() = _binding!!

    private val duaAdapter = DuaAdapter()
    private var allDuas: List<Dua> = emptyList()
    private var selectedCategory = ALL_CATEGORIES
    private var searchQuery = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDuaCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDuaList()
        setupSearch()
        loadDuas()
    }

    private fun setupDuaList() {
        binding.recyclerViewDuas.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewDuas.adapter = duaAdapter
    }

    private fun setupSearch() {
        binding.etDuaSearch.addTextChangedListener { text ->
            searchQuery = text.toString()
            applyFilters()
        }
    }

    private fun loadDuas() {
        val duaDao = DuaDatabase.getDatabase(requireContext()).duaDao()
        viewLifecycleOwner.lifecycleScope.launch {
            duaDao.getAllDuas().collectLatest { duas ->
                allDuas = duas
                renderCategoryChips(duas)
                applyFilters()
            }
        }
    }

    private fun renderCategoryChips(duas: List<Dua>) {
        val categories = listOf(ALL_CATEGORIES) + duas.map { it.category }.distinct()
        binding.chipGroupCategories.removeAllViews()

        categories.forEach { category ->
            val chip = Chip(requireContext()).apply {
                id = View.generateViewId()
                text = category
                isCheckable = true
                isChecked = category == selectedCategory
                contentDescription = "Show $category duas"
                setTextColor(resources.getColor(android.R.color.white, null))
                chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor(if (category == selectedCategory) "#2D402B" else "#79AE6F")
                )
                setOnClickListener {
                    selectedCategory = category
                    renderCategoryChips(allDuas)
                    applyFilters()
                }
            }
            binding.chipGroupCategories.addView(chip)
        }
    }

    private fun applyFilters() {
        val normalizedQuery = searchQuery.trim().lowercase(Locale.ROOT)
        val filteredDuas = allDuas.filter { dua ->
            val matchesCategory = selectedCategory == ALL_CATEGORIES || dua.category == selectedCategory
            val matchesSearch = normalizedQuery.isBlank() ||
                dua.title.lowercase(Locale.ROOT).contains(normalizedQuery) ||
                dua.category.lowercase(Locale.ROOT).contains(normalizedQuery) ||
                dua.translation.lowercase(Locale.ROOT).contains(normalizedQuery) ||
                dua.transliteration.lowercase(Locale.ROOT).contains(normalizedQuery) ||
                dua.source.lowercase(Locale.ROOT).contains(normalizedQuery) ||
                dua.arabicText.contains(searchQuery.trim())

            matchesCategory && matchesSearch
        }

        duaAdapter.submitList(filteredDuas)
        binding.tvDuaCount.text = resources.getQuantityString(
            com.example.bayyinly.R.plurals.dua_count,
            filteredDuas.size,
            filteredDuas.size
        )
        binding.tvEmptyDuas.isVisible = filteredDuas.isEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ALL_CATEGORIES = "All"
    }
}
