package com.kazanion.ui.home

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kazanion.databinding.ItemPollBinding
import com.kazanion.model.Poll

class ActivePollsAdapter(
    private val onItemClick: (Poll) -> Unit,
    private val onJoinClick: (Poll) -> Unit
) : ListAdapter<Poll, ActivePollsAdapter.PollViewHolder>(PollDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollViewHolder {
        val binding = ItemPollBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PollViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PollViewHolder, position: Int) {
        val poll = getItem(position)
        holder.bind(poll)
    }

    inner class PollViewHolder(private val binding: ItemPollBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(adapterPosition))
                }
            }
            binding.buttonJoinPoll.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onJoinClick(getItem(adapterPosition))
                }
            }
        }

        fun bind(poll: Poll) {
            binding.textViewPollTitle.text = poll.title
            binding.textViewPollDescription.text = poll.description
            binding.textViewPollPoints.text = "${poll.points} Puan"
        }
    }

    class PollDiffCallback : DiffUtil.ItemCallback<Poll>() {
        override fun areItemsTheSame(oldItem: Poll, newItem: Poll): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Poll, newItem: Poll): Boolean {
            return oldItem == newItem
        }
    }
}