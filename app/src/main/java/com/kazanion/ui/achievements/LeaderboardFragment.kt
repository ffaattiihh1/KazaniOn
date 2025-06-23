package com.kazanion.ui.achievements

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kazanion.databinding.FragmentLeaderboardBinding
import com.kazanion.network.ApiService
import kotlinx.coroutines.launch

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiService = ApiService.create()

        binding.recyclerViewFullLeaderboard.layoutManager = LinearLayoutManager(context)

        loadFullLeaderboard()
    }

    private fun loadFullLeaderboard() {
        lifecycleScope.launch {
            try {
                val leaderboard = apiService.getLeaderboard()
                val adapter = LeaderboardAdapter(leaderboard)
                binding.recyclerViewFullLeaderboard.adapter = adapter
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error appropriately
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 