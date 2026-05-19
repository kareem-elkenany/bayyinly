package com.example.bayyinly.ui.tasbih

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bayyinly.database.AzkarDatabase
import com.example.bayyinly.databinding.FragmentAzkarDetailBinding
import com.example.bayyinly.repository.AzkarRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

        val dbCategory = arguments?.getString("dbCategory") ?: return
        val title      = arguments?.getString("title") ?: dbCategory
        val colorHex   = arguments?.getString("colorHex") ?: "#79AE6F"

        binding.tvDetailTitle.text = title
        binding.btnDetailBack.setOnClickListener { findNavController().navigateUp() }

        val repo = AzkarRepository(AzkarDatabase.getDatabase(requireContext()).azkarDao())

        binding.rvAzkarEntries.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            repo.getEntriesByCategory(dbCategory).collectLatest { entries ->
                binding.tvDetailSubtitle.text = "${entries.size} azkar"
                binding.rvAzkarEntries.adapter = AzkarEntryAdapter(entries, colorHex)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
