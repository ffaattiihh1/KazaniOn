package com.kazanion.ui.achievements

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kazanion.R
import com.kazanion.databinding.FragmentAchievementsBinding
import com.kazanion.network.ApiService
import com.kazanion.model.Achievement
import com.kazanion.model.LeaderboardEntry
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController

class AchievementsFragment : Fragment() {

    private var _binding: FragmentAchievementsBinding? = null
    private val binding get() = _binding!!
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAchievementsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        apiService = ApiService.create()

        // Setup RecyclerViews
        binding.recyclerViewAchievements.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewLeaderboard.layoutManager = LinearLayoutManager(context)

        // Load achievements and leaderboard
        loadAchievements()
        loadLeaderboard()

        binding.leaderboardCard.setOnClickListener {
            findNavController().navigate(R.id.action_achievementsFragment_to_leaderboardFragment)
        }

        return root
    }

    private fun loadAchievements() {
        lifecycleScope.launch {
            try {
                // Get all achievements and user's earned achievements
                val allAchievements = apiService.getAllAchievements()
                
                // Use the current logged-in user's ID (you should get this from your auth system)
                val currentUserId = getCurrentUserId() // TODO: Implement this method
                val userAchievements = apiService.getUserAchievements(currentUserId)

                // Create a map of earned achievement IDs for quick lookup
                val earnedAchievementIds = userAchievements.map { it.id }.toSet()

                // Combine achievements with earned status
                val achievementsWithStatus = allAchievements.map { achievement ->
                    val userAchievement = userAchievements.find { it.id == achievement.id }
                    achievement.copy(
                        isEarned = earnedAchievementIds.contains(achievement.id),
                        earnedAt = userAchievement?.earnedAt,
                        pointsEarned = userAchievement?.pointsEarned
                    )
                }

                // Create and set the adapter
                val adapter = AchievementsAdapter(achievementsWithStatus)
                binding.recyclerViewAchievements.adapter = adapter

            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    private fun loadLeaderboard() {
        lifecycleScope.launch {
            try {
                val leaderboard = apiService.getLeaderboard().take(3) // Show only top 3
                val adapter = LeaderboardAdapter(leaderboard)
                binding.recyclerViewLeaderboard.adapter = adapter
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getCurrentUserId(): Long {
        // TODO: Implement this to get the current logged-in user's ID
        // For now, return a default user ID - you should get this from your auth system
        return 1L // Replace with actual user ID from your auth system
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 