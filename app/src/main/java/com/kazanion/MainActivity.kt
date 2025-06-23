package com.kazanion

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.kazanion.databinding.ActivityMainBinding
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.NavDestination

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var isLoggedIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide action bar
        supportActionBar?.hide()

        // Check login status
        val sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        // Setup Navigation
        setupNavigation()

        // If logged in, navigate to home
        if (isLoggedIn) {
            navController.navigate(R.id.homeFragment)
        }
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Define top level destinations for bottom navigation
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.navigation_polls,
                R.id.navigation_explore,
                R.id.navigation_achievements,
                R.id.navigation_profile
            )
        )

        // Setup bottom navigation with nav controller
        // binding.bottomNavigation.setupWithNavController(navController)

        // Custom navigation to prevent wrong fragment redirection
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navController.popBackStack(R.id.homeFragment, false)
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.navigation_polls -> {
                    navController.popBackStack(R.id.navigation_polls, false)
                    navController.navigate(R.id.navigation_polls)
                    true
                }
                R.id.navigation_explore -> {
                    navController.popBackStack(R.id.navigation_explore, false)
                    navController.navigate(R.id.navigation_explore)
                    true
                }
                R.id.navigation_achievements -> {
                    navController.popBackStack(R.id.navigation_achievements, false)
                    navController.navigate(R.id.navigation_achievements)
                    true
                }
                R.id.navigation_profile -> {
                    navController.popBackStack(R.id.navigation_profile, false)
                    navController.navigate(R.id.navigation_profile)
                    true
                }
                else -> false
            }
        }

        // Handle navigation visibility and behavior
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Update login status
            isLoggedIn = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
                .getBoolean("isLoggedIn", false)

            // Show/hide bottom navigation based on destination
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    binding.bottomNavigation.visibility = View.GONE
                }
                else -> {
                    if (isLoggedIn) {
                        binding.bottomNavigation.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Check login status when activity resumes
        isLoggedIn = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
            .getBoolean("isLoggedIn", false)
        
        if (isLoggedIn) {
            binding.bottomNavigation.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        // Handle back button press
        if (navController.currentDestination?.id == R.id.homeFragment) {
            // If we're on home, exit the app
            super.onBackPressed()
        } else {
            // Navigate back to home
            navController.navigate(R.id.homeFragment)
        }
    }

    private fun isMainScreen(destinationId: Int): Boolean {
        return when (destinationId) {
            R.id.homeFragment,
            R.id.navigation_polls,
            R.id.navigation_explore,
            R.id.navigation_achievements,
            R.id.navigation_profile -> true
            else -> false
        }
    }
} 