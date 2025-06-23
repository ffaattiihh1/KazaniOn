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
        lifecycleScope.launch {
            try {
                val polls = apiService.getActiveLinkPolls()
                pollsAdapter = PollsAdapter(polls)
                binding.recyclerViewPolls.adapter = pollsAdapter
                if (polls.isEmpty()) {
                    binding.cardViewNoPolls.visibility = View.VISIBLE
                } else {
                    binding.cardViewNoPolls.visibility = View.GONE
                }
            } catch (e: Exception) {
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