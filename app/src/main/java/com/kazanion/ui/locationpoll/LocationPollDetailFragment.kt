package com.kazanion.ui.locationpoll

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kazanion.R
import com.kazanion.databinding.FragmentLocationPollDetailBinding
import com.kazanion.network.LocationPoll

class LocationPollDetailFragment : Fragment() {
    private var _binding: FragmentLocationPollDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocationPollDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val poll = arguments?.getSerializable("poll") as? LocationPoll ?: return
        binding.textViewTitle.text = poll.title
        binding.textViewReward.text = "${poll.points} TL"
        binding.textViewDistance.text = arguments?.getString("distance") ?: ""
        binding.textViewAddress.text = arguments?.getString("address") ?: ""
        binding.textViewDescription.text = parseColoredTags(poll.description)
        binding.buttonTakePoll.setOnClickListener {
            Toast.makeText(requireContext(), "Anketi Al tıklandı", Toast.LENGTH_SHORT).show()
        }
    }

    private fun parseColoredTags(text: String?): SpannableStringBuilder {
        if (text.isNullOrEmpty()) return SpannableStringBuilder("")
        val builder = SpannableStringBuilder()
        var i = 0
        while (i < text.length) {
            when {
                text.startsWith("[blue]", i) -> {
                    val end = text.indexOf("[/blue]", i)
                    if (end != -1) {
                        val content = text.substring(i + 6, end)
                        val start = builder.length
                        builder.append(content)
                        builder.setSpan(
                            ForegroundColorSpan(resources.getColor(R.color.kazanion_blue)),
                            start, start + content.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        i = end + 7
                        continue
                    }
                }
                text.startsWith("[red]", i) -> {
                    val end = text.indexOf("[/red]", i)
                    if (end != -1) {
                        val content = text.substring(i + 5, end)
                        val start = builder.length
                        builder.append(content)
                        builder.setSpan(
                            ForegroundColorSpan(Color.parseColor("#FF3B30")),
                            start, start + content.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        i = end + 6
                        continue
                    }
                }
                else -> {
                    builder.append(text[i])
                    i++
                }
            }
        }
        return builder
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 