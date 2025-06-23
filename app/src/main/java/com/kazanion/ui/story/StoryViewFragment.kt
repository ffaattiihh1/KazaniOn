package com.kazanion.ui.story

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kazanion.databinding.FragmentStoryViewBinding

class StoryViewFragment : Fragment() {
    private var _binding: FragmentStoryViewBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())
    private val STORY_DURATION = 5000L // 5 saniye

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hikaye verilerini al
        val storyTitle = arguments?.getString("storyTitle") ?: "Duyuru"
        val storyDescription = arguments?.getString("storyDescription") ?: "Liderler Kazanıyor!"
        val storyImageUrl = arguments?.getString("storyImageUrl")

        // Hikaye içeriğini ayarla
        binding.storyTitle.text = storyTitle
        binding.storyDescription.text = storyDescription

        // Hikaye resmini yükle
        storyImageUrl?.let { url ->
            Glide.with(this)
                .load(url)
                .into(binding.storyImage)
        }

        // İlerleme çubuğunu başlat
        startStoryProgress()

        // Kapatma butonunu ayarla
        binding.closeButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // Ekrana tıklandığında hikayeyi kapat
        binding.root.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun startStoryProgress() {
        binding.storyProgressBar.progress = 0
        val updateInterval = 50L // 50ms'de bir güncelle
        val totalSteps = STORY_DURATION / updateInterval
        var currentStep = 0

        val updateProgress = object : Runnable {
            override fun run() {
                currentStep++
                val progress = (currentStep * 100 / totalSteps).toInt()
                binding.storyProgressBar.progress = progress

                if (currentStep < totalSteps) {
                    handler.postDelayed(this, updateInterval)
                } else {
                    findNavController().navigateUp()
                }
            }
        }

        handler.post(updateProgress)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
} 