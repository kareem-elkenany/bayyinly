package com.example.bayyinly.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.bayyinly.R
import com.example.bayyinly.databinding.ActivityMainBinding
import android.view.View

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Get the Navigation Host Fragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // 2. Get the NavController (the brain of navigation)
        val navController = navHostFragment.navController

        // 3. Link the Bottom Navigation View to the NavController
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.readingFragment) { // Use your actual Fragment ID here
                // Hide it on the Reading Screen
                binding.bottomNav.visibility = View.GONE
            } else {
                // Show it everywhere else (Home, List, Settings, etc.)
                binding.bottomNav.visibility = View.VISIBLE
            }
        }
    }
}