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
import com.google.firebase.auth.FirebaseAuth

class AchievementsFragment : Fragment() {

    private var _binding: FragmentAchievementsBinding? = null
    private val binding get() = _binding!!
    private lateinit var apiService: ApiService
    private lateinit var achievementViewModel: AchievementViewModel

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

        binding.leaderboardCard.setOnClickListener {
            findNavController().navigate(R.id.action_achievementsFragment_to_leaderboardFragment)
        }
    }

    private fun setupObservers() {
        achievementViewModel.userRanking.observe(viewLifecycleOwner) { ranking ->
            android.util.Log.d("AchievementsFragment", "User ranking received: $ranking")
            updateUserRankingCard(ranking)
        }

        achievementViewModel.leaderboard.observe(viewLifecycleOwner) { leaderboard ->
            android.util.Log.d("AchievementsFragment", "Leaderboard received: ${leaderboard?.size} entries")
            val topLeaderboard = leaderboard?.take(10) ?: emptyList()
            val currentUserId = getCurrentUserId()
            val adapter = LeaderboardAdapter(topLeaderboard, currentUserId)
            binding.recyclerViewLeaderboard.adapter = adapter
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
        
        if (currentUserId != null) {
            achievementViewModel.loadUserRanking(currentUserId)
            achievementViewModel.loadUserAchievements(currentUserId)
        }
        achievementViewModel.loadLeaderboard(50)
        achievementViewModel.loadAllAchievements()
        
        // Load user info for display
        loadUserDisplayInfo()
    }

    private fun updateUserRankingCard(ranking: com.kazanion.model.UserRanking?) {
        android.util.Log.d("AchievementsFragment", "Updating user ranking card: $ranking")
        
        if (ranking != null) {
            binding.textViewUserRank.text = "#${ranking.rank}"
            binding.textViewUserPoints.text = "${ranking.points} puan"
            binding.textViewUserDisplayName.text = ranking.displayName
            
            // Update rank circle color based on ranking
            when (ranking.badge) {
                "gold" -> {
                    binding.textViewUserRank.setBackgroundResource(R.drawable.gold_circle_background)
                    binding.textViewUserRank.setTextColor(requireContext().getColor(android.R.color.black))
                    binding.textViewUserRank.text = "🥇"
                }
                "silver" -> {
                    binding.textViewUserRank.setBackgroundResource(R.drawable.silver_circle_background)
                    binding.textViewUserRank.setTextColor(requireContext().getColor(android.R.color.black))
                    binding.textViewUserRank.text = "🥈"
                }
                "bronze" -> {
                    binding.textViewUserRank.setBackgroundResource(R.drawable.bronze_circle_background)
                    binding.textViewUserRank.setTextColor(requireContext().getColor(android.R.color.black))
                    binding.textViewUserRank.text = "🥉"
                }
                else -> {
                    binding.textViewUserRank.setBackgroundResource(R.drawable.white_circle_background)
                    binding.textViewUserRank.setTextColor(requireContext().getColor(android.R.color.black))
                    binding.textViewUserRank.text = "#${ranking.rank}"
                }
            }
        } else {
            // No ranking data available
            binding.textViewUserRank.text = "#-"
            binding.textViewUserRank.setBackgroundResource(R.drawable.white_circle_background)
            binding.textViewUserRank.setTextColor(requireContext().getColor(android.R.color.black))
            binding.textViewUserPoints.text = "0 puan"
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
        // Önce SharedPreferences'dan kontrol et
        val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val savedDisplayName = sharedPreferences.getString("userDisplayName", "")
        val savedEmail = sharedPreferences.getString("userEmail", "")
        
        if (!savedDisplayName.isNullOrEmpty()) {
            return savedDisplayName.split(" ").firstOrNull() ?: "Kullanıcı"
        }
        
        if (!savedEmail.isNullOrEmpty()) {
            return savedEmail.split("@").firstOrNull() ?: "Kullanıcı"
        }
        
        // Firebase'dan dene
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        
        if (currentUser != null) {
            val displayName = currentUser.displayName ?: ""
            return if (displayName.isNotEmpty()) {
                displayName.split(" ").firstOrNull() ?: currentUser.email?.split("@")?.firstOrNull() ?: "Kullanıcı"
            } else {
                currentUser.email?.split("@")?.firstOrNull() ?: "Kullanıcı"
            }
        }
        
        return "Kullanıcı"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 