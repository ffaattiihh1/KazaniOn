package com.kazanion

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.kazanion.databinding.ActivityMainBinding
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.NavDestination
import com.kazanion.utils.NotificationPermissionHelper
import com.kazanion.services.NotificationPollingService

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var isLoggedIn: Boolean = false
    private lateinit var notificationPermissionHelper: NotificationPermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide action bar
        supportActionBar?.hide()

        // Initialize notification permission helper
        notificationPermissionHelper = NotificationPermissionHelper(this)

        // Check login status
        val sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        // Setup Navigation
        setupNavigation()

        // Navigate based on login status
        if (isLoggedIn) {
            navController.navigate(R.id.homeFragment)
            // Kullanıcı zaten giriş yapmışsa notification service'ini başlat
            NotificationPollingService.startPolling(this)
        } else {
            navController.navigate(R.id.loginFragment)
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

        // Setup bottom navigation with nav controller - HIZLI NAVİGATİON!
        binding.bottomNavigation.setupWithNavController(navController)

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
                    // If user is not logged in and trying to access protected screens, redirect to login
                    if (!isLoggedIn && destination.id != R.id.loginFragment && destination.id != R.id.registerFragment) {
                        navController.navigate(R.id.loginFragment)
                        return@addOnDestinationChangedListener
                    }
                    
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
        // Handle back button press based on current destination
        when (navController.currentDestination?.id) {
            R.id.homeFragment -> {
                // If we're on home, exit the app
                super.onBackPressed()
            }
            R.id.registerFragment -> {
                // From register, go back to login
                navController.navigate(R.id.loginFragment)
            }
            R.id.loginFragment -> {
                // From login, exit the app
                super.onBackPressed()
            }
            else -> {
                // For other fragments, use default back behavior
                if (!navController.popBackStack()) {
                    super.onBackPressed()
                }
            }
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
    
    // Ana sayfadan bottom tab seçimi için
    fun selectBottomTab(tabId: Int) {
        binding.bottomNavigation.selectedItemId = tabId
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (!notificationPermissionHelper.isNotificationPermissionGranted()) {
            notificationPermissionHelper.requestNotificationPermission(this) { granted ->
                if (granted) {
                    Toast.makeText(this, "Bildirimler etkinleştirildi! 🔔", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Bildirimleri kaçırmamak için ayarlardan izni etkinleştirebilirsiniz", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        notificationPermissionHelper.handlePermissionResult(requestCode, permissions, grantResults) { granted ->
            if (granted) {
                Toast.makeText(this, "Bildirimler etkinleştirildi! 🔔", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Bildirimleri kaçırmamak için ayarlardan izni etkinleştirebilirsiniz", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Login işlemi başarılı olduktan sonra çağrılacak
    fun onLoginSuccess() {
        requestNotificationPermissionIfNeeded()
        // Bildirim polling service'ini başlat
        NotificationPollingService.startPolling(this)
    }
} 