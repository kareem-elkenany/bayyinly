package com.example.bayyinly.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bayyinly.R
import com.example.bayyinly.database.QuranDatabase
import com.example.bayyinly.databinding.FragmentHomeBinding
import com.example.bayyinly.network.RetrofitClient
import com.example.bayyinly.repository.PrayerRepository
import com.example.bayyinly.repository.UserStatsRepository
import com.example.bayyinly.viewmodel.HomeViewModel
import com.example.bayyinly.viewmodel.HomeViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    private val colorActive = Color.parseColor("#79AE6F")
    private val colorInactive = Color.parseColor("#A39A8A")
    private val colorDark = Color.parseColor("#2D402B")

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Permission Launcher for Location
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            getDeviceLocation()
        } else {
            // FALLBACK: User denied permission, use Cairo coordinates
            viewModel.loadHomeData(30.0444, 31.2357)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        observeViewModel()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        checkLocationPermissions()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            binding.cvAskAi.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top + (24 * resources.displayMetrics.density).toInt()
            }

            binding.llCurrentPrayer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top + (80 * resources.displayMetrics.density).toInt()
            }

            windowInsets
        }

        binding.cvAskAi.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_nav_chat)
        }

        binding.cvKhatmaCard.setOnLongClickListener {
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Reset Khatma?")
                .setMessage("Are you sure you want to restart your progress? This will wipe your read history and set you back to 0%. This cannot be undone.")
                .setPositiveButton("Reset") { dialog, _ ->
                    viewModel.resetProgress()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            true
        }
    }

    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted
            getDeviceLocation()
        } else {
            // Request permissions
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun getDeviceLocation() {
        // FIX: The compiler requires this explicit check right before calling lastLocation
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    // Success! Pass actual coordinates to ViewModel
                    viewModel.loadHomeData(location.latitude, location.longitude)
                } else {
                    // Fallback to Cairo if location is null (common on fresh emulators)
                    viewModel.loadHomeData(30.0444, 31.2357)
                }
            }
        } else {
            // Fallback if permissions are missing
            viewModel.loadHomeData(30.0444, 31.2357)
        }
    }

    private fun setupViewModel() {
        val db = QuranDatabase.getDatabase(requireContext())
        val prayerApi = RetrofitClient.prayerApiService
        val prayerRepo = PrayerRepository(prayerApi)
        val statsRepo = UserStatsRepository(db.userStatsDao())
        val factory = HomeViewModelFactory(prayerRepo, statsRepo)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.nextPrayerName.collectLatest { name ->
                    binding.tvPrayerName.text = name
                    updateTimelineHighlight(name)
                }
            }

            launch {
                viewModel.nextPrayerTime.collectLatest { time ->
                    binding.tvPrayerTime.text = formatTo12Hour(time)
                }
            }

            launch {
                viewModel.countdownText.collectLatest { timerValue ->
                    binding.tvCountdown.text = "- $timerValue"
                }
            }

            launch {
                viewModel.khatmaProgress.collectLatest { progress ->
                    binding.tvKhatmaPercentage.text = "$progress%"
                }
            }

            launch {
                viewModel.streak.collectLatest { days ->
                    binding.tvStreakDays.text = "$days Days"
                }
            }

            launch {
                viewModel.prayerTimings.collectLatest { timings ->
                    timings?.let {
                        binding.tvTimelineFajr.text = "Fajr\n${formatTo12Hour(it.fajr)}"
                        binding.tvTimelineDhuhr.text = "Dhuhr\n${formatTo12Hour(it.dhuhr)}"
                        binding.tvTimelineAsr.text = "Asr\n${formatTo12Hour(it.asr)}"
                        binding.tvTimelineMaghrib.text = "Maghrib\n${formatTo12Hour(it.maghrib)}"
                        binding.tvTimelineIsha.text = "Isha\n${formatTo12Hour(it.isha)}"
                    }
                }
            }
        }
    }

    private fun formatTo12Hour(time24: String): String {
        return try {
            val cleanTime = time24.split(" ")[0]
            val sdf24 = SimpleDateFormat("HH:mm", Locale.getDefault())
            val sdf12 = SimpleDateFormat("h:mm a", Locale.getDefault())
            val date = sdf24.parse(cleanTime)
            sdf12.format(date!!)
        } catch (e: Exception) {
            time24
        }
    }

    private fun updateTimelineHighlight(nextPrayer: String) {
        val allTexts = listOf(binding.tvTimelineFajr, binding.tvTimelineDhuhr, binding.tvTimelineAsr, binding.tvTimelineMaghrib, binding.tvTimelineIsha)
        val allNodes = listOf(binding.nodeFajr, binding.nodeDhuhr, binding.nodeAsr, binding.nodeMaghrib, binding.nodeIsha)

        allTexts.forEach { it.setTextColor(colorInactive); it.typeface = android.graphics.Typeface.DEFAULT }
        allNodes.forEach {
            it.setCardBackgroundColor(colorInactive)
            it.strokeWidth = 0
            val params = it.layoutParams
            params.width = (12 * resources.displayMetrics.density).toInt()
            params.height = (12 * resources.displayMetrics.density).toInt()
            it.layoutParams = params
        }

        when (nextPrayer) {
            "Dhuhr" -> {
                highlightNode(binding.tvTimelineFajr, binding.nodeFajr, colorActive)
                binding.pbTimelineProgress.progress = 15
            }
            "Asr" -> {
                highlightNode(binding.tvTimelineDhuhr, binding.nodeDhuhr, colorActive)
                binding.pbTimelineProgress.progress = 35
            }
            "Maghrib" -> {
                highlightNode(binding.tvTimelineAsr, binding.nodeAsr, colorActive)
                binding.pbTimelineProgress.progress = 55
            }
            "Isha" -> {
                highlightNode(binding.tvTimelineMaghrib, binding.nodeMaghrib, colorDark)
                binding.pbTimelineProgress.progress = 75
            }
            "Fajr" -> {
                highlightNode(binding.tvTimelineIsha, binding.nodeIsha, colorDark)
                binding.pbTimelineProgress.progress = 95
            }
        }
    }

    private fun highlightNode(textView: android.widget.TextView, node: com.google.android.material.card.MaterialCardView, activeColor: Int) {
        textView.setTextColor(activeColor)
        textView.textSize = 14f
        textView.typeface = android.graphics.Typeface.DEFAULT_BOLD

        node.setCardBackgroundColor(activeColor)
        node.strokeWidth = (2 * resources.displayMetrics.density).toInt()
        node.strokeColor = Color.WHITE

        val params = node.layoutParams
        params.width = (16 * resources.displayMetrics.density).toInt()
        params.height = (16 * resources.displayMetrics.density).toInt()
        node.layoutParams = params
        node.radius = (8 * resources.displayMetrics.density)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}