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
        
        // Loading state göster
        if (_binding != null && isAdded) {
            binding.cardViewNoPolls.visibility = View.GONE
            // TODO: Loading animation ekle
        }
        
        lifecycleScope.launch {
            try {
                android.util.Log.d("PollsFragment", "Calling getActiveLinkPolls API...")
                val polls = apiService.getActiveLinkPolls()
                android.util.Log.d("PollsFragment", "✅ SUCCESS: Received polls count: ${polls.size}")
                polls.forEachIndexed { index, poll ->
                    android.util.Log.d("PollsFragment", "Poll $index: ${poll.title} - Link: ${poll.link}")
                }
                
                // Fragment hala aktif mi kontrol et
                if (_binding != null && isAdded) {
                    pollsAdapter = PollsAdapter(polls)
                    binding.recyclerViewPolls.adapter = pollsAdapter
                    if (polls.isEmpty()) {
                        android.util.Log.d("PollsFragment", "No polls found - showing empty view")
                        binding.cardViewNoPolls.visibility = View.VISIBLE
                    } else {
                        android.util.Log.d("PollsFragment", "✅ Polls loaded successfully - hiding empty view")
                        binding.cardViewNoPolls.visibility = View.GONE
                    }
                } else {
                    android.util.Log.d("PollsFragment", "Fragment not active, skipping UI update")
                }
            } catch (e: Exception) {
                android.util.Log.e("PollsFragment", "=== POLLS API ERROR ===")
                android.util.Log.e("PollsFragment", "Error Type: ${e.javaClass.simpleName}")
                android.util.Log.e("PollsFragment", "Error Message: ${e.message}")
                android.util.Log.e("PollsFragment", "Stack Trace:", e)
                
                var userMessage = "Anketler yüklenirken hata oluştu"
                
                // HTTP error ise detaylı bilgi
                if (e is retrofit2.HttpException) {
                    android.util.Log.e("PollsFragment", "HTTP Status Code: ${e.code()}")
                    android.util.Log.e("PollsFragment", "HTTP Error Body: ${e.response()?.errorBody()?.string()}")
                    userMessage = "Sunucu hatası (${e.code()})"
                }
                
                // Timeout error ise - Backend uyuyor
                if (e is java.net.SocketTimeoutException) {
                    android.util.Log.e("PollsFragment", "⚠️ TIMEOUT ERROR - Backend probably sleeping!")
                    userMessage = "Sunucu uyandırılıyor, lütfen tekrar deneyin..."
                    
                    // 3 saniye sonra otomatik retry
                    lifecycleScope.launch {
                        kotlinx.coroutines.delay(3000)
                        if (_binding != null && isAdded) {
                            android.util.Log.d("PollsFragment", "🔄 Auto-retrying after timeout...")
                            loadLinkPolls()
                        }
                    }
                }
                
                // Network error ise
                if (e is java.net.UnknownHostException || e is java.net.ConnectException) {
                    userMessage = "İnternet bağlantısını kontrol edin"
                }
                
                android.util.Log.e("PollsFragment", "👤 User message: $userMessage")
                
                // Fragment hala aktif mi kontrol et
                if (_binding != null && isAdded) {
                    binding.cardViewNoPolls.visibility = View.VISIBLE
                    // TODO: Error message'ı UI'da göster
                } else {
                    android.util.Log.d("PollsFragment", "Fragment not active, skipping error UI update")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 