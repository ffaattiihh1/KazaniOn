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
    
    // CACHE - Sayfa değişiminde hızlı açılması için
    companion object {
        private var cachedPolls: List<Poll>? = null
        private var lastLoadTime: Long = 0
        private const val CACHE_TIMEOUT = 60 * 1000L // 1 dakika cache
    }

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
        
        // ÖNCE CACHE KONTROL ET - HIZLI GÖSTERİM!
        val currentTime = System.currentTimeMillis()
        val cacheValid = cachedPolls != null && (currentTime - lastLoadTime) < CACHE_TIMEOUT
        
        if (cacheValid) {
            android.util.Log.d("PollsFragment", "✅ CACHE kullanılıyor - Hızlı açılış!")
            pollsAdapter = PollsAdapter(cachedPolls!!)
            binding.recyclerViewPolls.adapter = pollsAdapter
            binding.cardViewNoPolls.visibility = if (cachedPolls!!.isEmpty()) View.VISIBLE else View.GONE
        } else {
            android.util.Log.d("PollsFragment", "🔄 Cache yok, API'den yükleniyor...")
            pollsAdapter = PollsAdapter(emptyList())
            binding.recyclerViewPolls.adapter = pollsAdapter
            loadLinkPolls()
        }
    }

    private fun loadLinkPolls() {
        android.util.Log.d("PollsFragment", "Loading polls from API...")
        
        if (_binding != null && isAdded) {
            binding.cardViewNoPolls.visibility = View.GONE
        }
        
        lifecycleScope.launch {
            try {
                val polls = apiService.getAllPolls()
                android.util.Log.d("PollsFragment", "✅ Received ${polls.size} polls")
                
                // CACHE'E KAYDET
                cachedPolls = polls
                lastLoadTime = System.currentTimeMillis()
                
                if (_binding != null && isAdded) {
                    pollsAdapter = PollsAdapter(polls)
                    binding.recyclerViewPolls.adapter = pollsAdapter
                    binding.cardViewNoPolls.visibility = if (polls.isEmpty()) View.VISIBLE else View.GONE
                }
            } catch (e: Exception) {
                android.util.Log.e("PollsFragment", "Error loading polls", e)
                
                if (_binding != null && isAdded) {
                    binding.cardViewNoPolls.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 