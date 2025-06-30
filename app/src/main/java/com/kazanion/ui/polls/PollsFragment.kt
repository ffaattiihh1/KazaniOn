package com.kazanion.ui.polls

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kazanion.databinding.FragmentPollsBinding
import com.kazanion.model.Poll
import com.kazanion.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PollsFragment : Fragment() {
    private var _binding: FragmentPollsBinding? = null
    private val binding get() = _binding!!
    private lateinit var pollsAdapter: PollsAdapter
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPollsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiService = ApiService.create()
        binding.recyclerViewPolls.layoutManager = LinearLayoutManager(requireContext())
        pollsAdapter = PollsAdapter(emptyList())
        binding.recyclerViewPolls.adapter = pollsAdapter
        loadLinkPolls()
    }

    private fun loadLinkPolls() {
        android.util.Log.d("PollsFragment", "=== LOADING LINK POLLS ===")
        lifecycleScope.launch {
            try {
                android.util.Log.d("PollsFragment", "Calling getActiveLinkPolls API...")
                val polls = apiService.getActiveLinkPolls()
                android.util.Log.d("PollsFragment", "Received polls count: ${polls.size}")
                polls.forEachIndexed { index, poll ->
                    android.util.Log.d("PollsFragment", "Poll $index: ${poll.title} - Link: ${poll.link}")
                }
                
                pollsAdapter = PollsAdapter(polls)
                binding.recyclerViewPolls.adapter = pollsAdapter
                if (polls.isEmpty()) {
                    android.util.Log.d("PollsFragment", "No polls found - showing empty view")
                    binding.cardViewNoPolls.visibility = View.VISIBLE
                } else {
                    android.util.Log.d("PollsFragment", "Polls found - hiding empty view")
                    binding.cardViewNoPolls.visibility = View.GONE
                }
            } catch (e: Exception) {
                android.util.Log.e("PollsFragment", "Error loading polls: ${e.message}", e)
                // Hata yönetimi
                binding.cardViewNoPolls.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 