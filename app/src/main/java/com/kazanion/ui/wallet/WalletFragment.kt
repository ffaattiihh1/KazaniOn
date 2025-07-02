package com.kazanion.ui.wallet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kazanion.databinding.FragmentWalletBinding
import com.kazanion.model.Poll
import com.kazanion.network.ApiService
import com.kazanion.network.CompletedPoll
import com.kazanion.network.UserBalance
import com.kazanion.ui.polls.PollsAdapter
import kotlinx.coroutines.launch

class WalletFragment : Fragment() {
    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!
    private lateinit var pollsAdapter: PollsAdapter
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiService = ApiService.create()
        setupRecyclerView()
        loadUserBalance()
        loadCompletedPolls()
    }

    private fun loadUserBalance() {
        // Yeni kullanıcı için "yeni_kullanici" kullanıyoruz
        lifecycleScope.launch {
            try {
                val userBalance = apiService.getUserBalance("yeni_kullanici")
                binding.textViewBalance.text = "%.2f TL".format(userBalance.balance)
                binding.textViewPoints.text = "${userBalance.points} Puan"
            } catch (e: Exception) {
                Log.e("WalletFragment", "Bakiye yükleme hatası", e)
            }
        }
    }

    private fun loadCompletedPolls() {
        // Yeni kullanıcı ID'si: 2
        lifecycleScope.launch {
            try {
                val completedPolls = apiService.getCompletedPolls(2)
                if (completedPolls.isEmpty()) {
                    binding.textViewEmptyState.visibility = View.VISIBLE
                    binding.recyclerViewPolls.visibility = View.GONE
                } else {
                    binding.textViewEmptyState.visibility = View.GONE
                    binding.recyclerViewPolls.visibility = View.VISIBLE
                    // For now, we'll create empty polls since we don't have the actual poll data
                    // TODO: Modify backend to return full poll data with completed polls
                    val polls = completedPolls.map { completedPoll ->
                        Poll(
                            id = completedPoll.pollId.toInt(),
                            title = "Completed Poll ${completedPoll.pollId}",
                            description = "This poll was completed",
                            points = 0, // 0 puan (backward compatibility)
                            isActive = false,
                            locationBased = false,
                            latitude = null,
                            longitude = null,
                            link = null,
                            createdAt = completedPoll.completedAt.toString()
                        )
                    }
                    pollsAdapter.updatePolls(polls)
                }
            } catch (e: Exception) {
                Log.e("WalletFragment", "Tamamlanmış anketler yükleme hatası", e)
                binding.textViewEmptyState.visibility = View.VISIBLE
                binding.recyclerViewPolls.visibility = View.GONE
            }
        }
    }

    private fun setupRecyclerView() {
        pollsAdapter = PollsAdapter(emptyList())
        binding.recyclerViewPolls.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pollsAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 