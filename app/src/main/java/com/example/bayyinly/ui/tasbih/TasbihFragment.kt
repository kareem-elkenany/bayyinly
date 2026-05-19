package com.example.bayyinly.ui.tasbih

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.bayyinly.databinding.FragmentTasbihBinding
import com.example.bayyinly.viewmodel.TasbihViewModel

class TasbihFragment : Fragment() {

    private var _binding: FragmentTasbihBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TasbihViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasbihBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBeadView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupBeadView() {
        binding.beadView.onSwipe = {
            viewModel.increment()
            performHapticFeedback()
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnReset.setOnClickListener { viewModel.reset() }
        binding.btnNextAzkar.setOnClickListener { viewModel.nextAzkar() }
    }

    private fun observeViewModel() {
        viewModel.count.observe(viewLifecycleOwner) { count ->
            binding.beadView.updateCount(count, viewModel.target.value ?: 33, viewModel.totalCount.value ?: 0)
        }

        viewModel.target.observe(viewLifecycleOwner) { target ->
            binding.beadView.updateCount(viewModel.count.value ?: 0, target, viewModel.totalCount.value ?: 0)
        }

        viewModel.totalCount.observe(viewLifecycleOwner) { total ->
            binding.tvTotalCount.text = total.toString()
            binding.beadView.updateCount(viewModel.count.value ?: 0, viewModel.target.value ?: 33, total)
        }

        viewModel.currentAzkar.observe(viewLifecycleOwner) { azkar ->
            binding.tvAzkarArabic.text = azkar?.zekr ?: ""
            binding.tvAzkarTranslation.text = azkar?.description ?: ""
        }
    }

    private fun performHapticFeedback() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
