package com.kazanion.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kazanion.databinding.ItemLocationPollBinding
import com.kazanion.network.LocationPoll
import kotlin.math.roundToInt

class LocationPollAdapter(private val onPollClick: (LocationPoll) -> Unit) :
    ListAdapter<LocationPoll, LocationPollAdapter.PollViewHolder>(PollDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollViewHolder {
        val binding = ItemLocationPollBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PollViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PollViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PollViewHolder(
        private val binding: ItemLocationPollBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onPollClick(getItem(position))
                }
            }
        }

        fun bind(poll: LocationPoll) {
            binding.apply {
                pollTitle.text = poll.title
                pollReward.text = "${poll.points} TL"
                // Mesafe hesaplaması burada yapılabilir
            }
        }
    }

    private class PollDiffCallback : DiffUtil.ItemCallback<LocationPoll>() {
        override fun areItemsTheSame(oldItem: LocationPoll, newItem: LocationPoll): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LocationPoll, newItem: LocationPoll): Boolean {
            return oldItem == newItem
        }
    }
} 