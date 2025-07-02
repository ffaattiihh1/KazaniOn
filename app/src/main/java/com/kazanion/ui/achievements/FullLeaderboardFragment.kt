package com.kazanion.ui.achievements

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kazanion.databinding.FragmentFullLeaderboardBinding
import com.kazanion.viewmodels.AchievementViewModel

class FullLeaderboardFragment : Fragment() {

    private var _binding: FragmentFullLeaderboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var achievementViewModel: AchievementViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFullLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        achievementViewModel = ViewModelProvider(this)[AchievementViewModel::class.java]
        
        setupUI()
        setupObservers()
        loadData()
    }

    private fun setupUI() {
        // Back button
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
        
        // RecyclerView setup
        binding.recyclerViewFullLeaderboard.layoutManager = LinearLayoutManager(context)
    }

    private fun setupObservers() {
        achievementViewModel.leaderboard.observe(viewLifecycleOwner) { leaderboard ->
            android.util.Log.d("FullLeaderboardFragment", "Full leaderboard received: ${leaderboard?.size} entries")
            
            val fullLeaderboardData = leaderboard ?: emptyList()
            val currentUserId = getCurrentUserId()
            val adapter = LeaderboardAdapter(fullLeaderboardData, currentUserId)
            binding.recyclerViewFullLeaderboard.adapter = adapter
            
            // Title güncelle
            binding.textViewTitle.text = "Sıralama (${fullLeaderboardData.size} Kullanıcı)"
        }

        achievementViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                achievementViewModel.onErrorShown()
            }
        }
    }

    private fun loadData() {
        // Tüm leaderboard'ı yükle
        achievementViewModel.loadLeaderboard(100)
    }

    private fun getCurrentUserId(): Long? {
        val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userId", null)?.toLongOrNull()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 