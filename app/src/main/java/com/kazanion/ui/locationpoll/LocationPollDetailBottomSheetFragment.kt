package com.kazanion.ui.locationpoll

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.kazanion.databinding.FragmentLocationPollDetailBottomSheetBinding
import com.kazanion.R
import com.kazanion.network.ApiService
import com.kazanion.network.LocationPoll
import com.kazanion.models.User
import com.kazanion.network.CompletePollRequest
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LocationPollDetailBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentLocationPollDetailBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var poll: LocationPoll
    private var onZoomToPoll: ((LatLng) -> Unit)? = null
    private lateinit var apiService: ApiService
    private var distance: String? = null
    private var address: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiService = ApiService.create()
        arguments?.let {
            poll = it.getParcelable("poll")!!
            distance = it.getString("distance")
            address = it.getString("address")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationPollDetailBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            textTitle.text = poll.title
            textDescription.text = poll.description
            textReward.text = "${poll.points} Puan"
            textDistance.text = distance ?: "N/A"
            textAddress.text = address ?: poll.description ?: "Adres bilgisi yok"

            buttonShowOnMap.setOnClickListener {
                val lat = poll.latitude
                val lng = poll.longitude
                if (lat != null && lng != null) {
                    onZoomToPoll?.invoke(LatLng(lat, lng))
                }
                dismiss()
            }

            buttonTakePoll.setOnClickListener {
                completePoll()
            }
        }
    }

    private fun completePoll() {
        // Geçici olarak sabit kullanıcı adı kullanıyoruz
        val username = "yeni_kullanici"
        
        lifecycleScope.launch {
            try {
                val user = apiService.getUserByUsername(username)
                finishCompletingPoll(user.id)
            } catch (e: Exception) {
                Toast.makeText(context, "Kullanıcı bilgileri alınamadı: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun finishCompletingPoll(userId: Long) {
        lifecycleScope.launch {
            try {
                val request = CompletePollRequest(userId)
                val response = apiService.completePoll(poll.id.toLong(), request)
                if (response.success) {
                    Toast.makeText(context, "Anket başarıyla tamamlandı! ${response.pointsEarned} puan kazandınız.", Toast.LENGTH_SHORT).show()
                    
                    // Check for new achievements after completing poll
                    checkAchievements(userId)
                    
                    dismiss()
                } else {
                    Toast.makeText(context, "Anket tamamlanamadı: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Ağ hatası: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("CompletePoll", "Failure", e)
            }
        }
    }

    private fun checkAchievements(userId: Long) {
        lifecycleScope.launch {
            try {
                val achievements = apiService.checkAchievements(userId)
                if (achievements.isNotEmpty()) {
                    val achievementMessages = achievements.values.joinToString("\n")
                    Toast.makeText(context, "Yeni başarımlar kazandınız!\n$achievementMessages", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("CheckAchievements", "Error checking achievements", e)
            }
        }
    }

    fun setOnZoomToPollListener(listener: (LatLng) -> Unit) {
        onZoomToPoll = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(poll: LocationPoll, distance: String? = null, address: String? = null): LocationPollDetailBottomSheetFragment {
            return LocationPollDetailBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("poll", poll)
                    putString("distance", distance)
                    putString("address", address)
                }
            }
        }
    }
} 