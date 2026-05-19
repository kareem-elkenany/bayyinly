package com.example.bayyinly.ui.tasbih

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bayyinly.R
import com.example.bayyinly.databinding.FragmentAzkarHubBinding
import com.example.bayyinly.model.AzkarData

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
        binding.rvCategories.adapter = AzkarCategoryAdapter(AzkarData.categories) { group ->
            val bundle = Bundle().apply { putString("categoryName", group.category.name) }
            findNavController().navigate(R.id.action_nav_azkar_to_nav_azkar_detail, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}