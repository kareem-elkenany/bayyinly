package com.example.bayyinly.ui.tasbih

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bayyinly.R
import com.example.bayyinly.database.AzkarDatabase
import com.example.bayyinly.databinding.FragmentAzkarHubBinding
import com.example.bayyinly.repository.AzkarRepository
import kotlinx.coroutines.launch

class AzkarHubFragment : Fragment() {

    private var _binding: FragmentAzkarHubBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAzkarHubBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardOpenTasbih.setOnClickListener {
            findNavController().navigate(R.id.action_nav_azkar_to_nav_tasbih)
        }

        binding.rvCategories.layoutManager = GridLayoutManager(requireContext(), 2)

        val repo = AzkarRepository(AzkarDatabase.getDatabase(requireContext()).azkarDao())

        viewLifecycleOwner.lifecycleScope.launch {
            val configs = CATEGORY_DISPLAY.map { base ->
                base.copy(itemCount = repo.getCountByCategory(base.dbCategory))
            }
            binding.rvCategories.adapter = AzkarCategoryAdapter(configs) { config ->
                val bundle = Bundle().apply {
                    putString("dbCategory", config.dbCategory)
                    putString("title", config.title)
                    putString("colorHex", config.colorHex)
                }
                findNavController().navigate(R.id.action_nav_azkar_to_nav_azkar_detail, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val CATEGORY_DISPLAY = listOf(
            AzkarCategoryConfig(AzkarRepository.MORNING,      "Morning Azkar",  R.drawable.ic_mosque,  "#F59E0B"),
            AzkarCategoryConfig(AzkarRepository.EVENING,      "Evening Azkar",  R.drawable.ic_mosque,  "#6366F1"),
            AzkarCategoryConfig(AzkarRepository.AFTER_PRAYER, "After Prayer",   R.drawable.ic_tasbih,  "#79AE6F"),
            AzkarCategoryConfig(AzkarRepository.SLEEP,        "Before Sleep",   R.drawable.ic_mosque,  "#8B5CF6"),
            AzkarCategoryConfig(AzkarRepository.GENERAL,      "General Dhikr",  R.drawable.ic_dua,     "#059669")
        )
    }
}
