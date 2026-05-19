package com.example.bayyinly.ui.tasbih

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bayyinly.databinding.FragmentAzkarDetailBinding
import com.example.bayyinly.model.AzkarCategory
import com.example.bayyinly.model.AzkarData

class AzkarDetailFragment : Fragment() {

    private var _binding: FragmentAzkarDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAzkarDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryName = arguments?.getString("categoryName") ?: return
        val category = AzkarCategory.valueOf(categoryName)
        val group = AzkarData.categories.first { it.category == category }

        binding.tvDetailTitle.text = category.title
        binding.tvDetailSubtitle.text = "${group.items.size} azkar"

        val accentColor = accentColor(category)
        binding.btnDetailBack.setOnClickListener { findNavController().navigateUp() }

        binding.rvAzkarEntries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAzkarEntries.adapter = AzkarEntryAdapter(group.items, accentColor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun accentColor(category: AzkarCategory): String = when (category) {
        AzkarCategory.MORNING      -> "#F59E0B"
        AzkarCategory.EVENING      -> "#6366F1"
        AzkarCategory.AFTER_PRAYER -> "#79AE6F"
        AzkarCategory.SLEEP        -> "#8B5CF6"
        AzkarCategory.GENERAL      -> "#059669"
    }
}