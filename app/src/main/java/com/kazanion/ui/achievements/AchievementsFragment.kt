package com.kazanion.ui.achievements

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kazanion.R
import com.kazanion.databinding.FragmentAchievementsBinding
import com.kazanion.network.ApiService
import com.kazanion.model.Achievement
import com.kazanion.model.LeaderboardEntry
import com.kazanion.viewmodels.AchievementViewModel
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController

class AchievementsFragment : Fragment() {

    private var _binding: FragmentAchievementsBinding? = null
    private val binding get() = _binding!!
    private lateinit var apiService: ApiService
    private lateinit var achievementViewModel: AchievementViewModel
    
    // Leaderboard verileri
    private var fullLeaderboardData: List<LeaderboardEntry> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAchievementsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        apiService = ApiService.create()
        achievementViewModel = ViewModelProvider(this)[AchievementViewModel::class.java]

        setupUI()
        setupObservers()
        loadData()

        return root
    }

    private fun setupUI() {
        // Setup RecyclerViews
        binding.recyclerViewAchievements.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewLeaderboard.layoutManager = LinearLayoutManager(context)
        
        // "Daha fazla" butonu click listener
        binding.textViewShowMoreLeaderboard.setOnClickListener {
            showFullLeaderboard()
        }
    }

    private fun setupObservers() {
        achievementViewModel.userRanking.observe(viewLifecycleOwner) { ranking ->
            android.util.Log.d("AchievementsFragment", "User ranking received: $ranking")
            updateUserRankingCard(ranking)
        }

        achievementViewModel.leaderboard.observe(viewLifecycleOwner) { leaderboard ->
            android.util.Log.d("AchievementsFragment", "Leaderboard received: ${leaderboard?.size} entries")
            
            fullLeaderboardData = leaderboard ?: emptyList()
            
            // İlk başta sadece top 3'ü göster
            val displayData = fullLeaderboardData.take(3)  // İlk 3 kullanıcı
            
            val currentUserId = getCurrentUserId()
            val adapter = LeaderboardAdapter(displayData, currentUserId)
            binding.recyclerViewLeaderboard.adapter = adapter
            
            // "Daha fazla" butonunu gizle/göster - 3'ten fazla kullanıcı varsa göster
            binding.textViewShowMoreLeaderboard.visibility = 
                if (fullLeaderboardData.size > 3) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                
            android.util.Log.d("AchievementsFragment", "📊 Leaderboard: ${fullLeaderboardData.size} users, showing ${displayData.size}, more button: ${binding.textViewShowMoreLeaderboard.visibility == View.VISIBLE}")
        }

        achievementViewModel.achievements.observe(viewLifecycleOwner) { achievements ->
            android.util.Log.d("AchievementsFragment", "Achievements received: ${achievements?.size} entries")
            achievements?.let {
                val adapter = AchievementsAdapter(it)
                binding.recyclerViewAchievements.adapter = adapter
            }
        }

        achievementViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                achievementViewModel.onErrorShown()
            }
        }
    }

    private fun loadData() {
        val currentUserId = getCurrentUserId()
        android.util.Log.d("AchievementsFragment", "Loading data for userId: $currentUserId")
        
        // DEBUG - SharedPreferences kontrol et
        val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        android.util.Log.d("AchievementsFragment", "🔍 DEBUG SharedPreferences:")
        sharedPreferences.all.forEach { (key, value) ->
            android.util.Log.d("AchievementsFragment", "   $key = $value")
        }
        
        if (currentUserId != null) {
            achievementViewModel.loadUserRanking(currentUserId)
            achievementViewModel.loadUserAchievements(currentUserId)
        } else {
            android.util.Log.w("AchievementsFragment", "⚠️ UserId is null! Cannot load user data")
        }
        achievementViewModel.loadLeaderboard(100)  // Daha fazla kullanıcı yükle
        achievementViewModel.loadAllAchievements()
        
        // Load user info for display
        loadUserDisplayInfo()
    }

    private fun updateUserRankingCard(ranking: com.kazanion.model.UserRanking?) {
        android.util.Log.d("AchievementsFragment", "Updating user ranking card: $ranking")
        
        if (ranking != null) {
            // YENİ TASARIM - Coin ve rank gösterimi
            binding.textViewUserDisplayName.text = ranking.displayName ?: "Kullanıcı"
            
            // GERÇEK COIN DEĞER - Points'den hesaplama
            val coins = ranking.points / 10  // 10 puan = 1 coin
            binding.textViewUserCoins.text = "$coins Coin"
            
            // Rank circle'ını güncelle
            when (ranking.badge) {
                "gold" -> {
                    binding.textViewUserRank.text = "🥇"
                    binding.textViewUserRank.setBackgroundResource(R.drawable.gold_circle_background)
                }
                "silver" -> {
                    binding.textViewUserRank.text = "🥈"
                    binding.textViewUserRank.setBackgroundResource(R.drawable.silver_circle_background)
                }
                "bronze" -> {
                    binding.textViewUserRank.text = "🥉"
                    binding.textViewUserRank.setBackgroundResource(R.drawable.bronze_circle_background)
                }
                else -> {
                    binding.textViewUserRank.text = "#${ranking.rank}"
                    binding.textViewUserRank.setBackgroundResource(R.drawable.white_circle_background)
                }
            }
            
        } else {
            // No ranking data available
            binding.textViewUserRank.text = "#-"
            binding.textViewUserRank.setBackgroundResource(R.drawable.white_circle_background)
            binding.textViewUserCoins.text = "0 Coin"
            binding.textViewUserDisplayName.text = getUserDisplayName()
        }
    }

    private fun getCurrentUserId(): Long? {
        val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userId", null)?.toLongOrNull()
    }

    private fun loadUserDisplayInfo() {
        val displayName = getUserDisplayName()
        binding.textViewUserDisplayName.text = displayName
    }

    private fun getUserDisplayName(): String {
        // Başarımlar sayfasında KULLANICI ADI göster
        val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")
        
        return if (!username.isNullOrEmpty()) {
            username  // Kullanıcı adını döndür
        } else {
            "kullanici"
        }
    }
    
    private fun showFullLeaderboard() {
        // Tam sıralama sayfasına git
        try {
            findNavController().navigate(R.id.action_achievementsFragment_to_fullLeaderboardFragment)
        } catch (e: Exception) {
            android.util.Log.e("AchievementsFragment", "Navigation error: ${e.message}")
            Toast.makeText(context, "Sıralama sayfası açılamıyor", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 