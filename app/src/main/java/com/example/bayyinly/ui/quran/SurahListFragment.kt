package com.example.bayyinly.ui.quran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bayyinly.databinding.FragmentSurahListBinding
import com.example.bayyinly.model.SurahData
import com.example.bayyinly.ui.quran.SurahAdapter
import androidx.navigation.fragment.findNavController
import androidx.core.os.bundleOf
import com.example.bayyinly.R
import androidx.core.widget.addTextChangedListener

class SurahListFragment : Fragment() {

    // ViewBinding setup for Fragments (prevents memory leaks)
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

        // 1. Give the RecyclerView its measuring tape (LinearLayoutManager)
        binding.recyclerViewSurahs.layoutManager = LinearLayoutManager(requireContext())

        // 2. Create the Adapter and pass our static list of 114 Surahs
        val adapter = SurahAdapter(SurahData.surahs) { clickedSurahId ->
            // Create a bundle to hold the ID
            val bundle = bundleOf("surahId" to clickedSurahId)

            // Navigate to the Reading screen, passing the bundle!
            findNavController().navigate(R.id.action_nav_quran_to_readingFragment, bundle)
        }

        // 3. Attach the Adapter to the UI
        binding.recyclerViewSurahs.adapter = adapter

        binding.etSearch.addTextChangedListener { text ->
            val query = text.toString().lowercase()

            // Filter the static list based on the search query
            val filteredList = SurahData.surahs.filter {
                it.englishName.lowercase().contains(query) ||
                        it.arabicName.contains(query)
            }

            // Give the new list to the adapter
            adapter.updateList(filteredList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the binding when the view is destroyed to free up memory
        _binding = null
    }
}