package com.example.chickcheckapp.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.chickcheckapp.R
import com.example.chickcheckapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
    }

    private fun setupView() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_login -> navView.visibility = View.GONE
                R.id.navigation_signup -> navView.visibility = View.GONE
                R.id.navigation_splash -> navView.visibility = View.GONE
                R.id.resultFragment -> navView.visibility = View.GONE
                R.id.navigation_scan -> navView.visibility = View.GONE
                R.id.resultFragment -> navView.visibility = View.GONE
                R.id.navigation_profile -> navView.visibility = View.GONE
                else -> navView.visibility = View.VISIBLE
            }
        }

    }
}