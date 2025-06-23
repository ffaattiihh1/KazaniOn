package com.kazanion.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kazanion.R
import com.kazanion.databinding.ItemStoryBinding
import com.kazanion.network.Story

class StoryAdapter(
    private val navController: NavController
) : ListAdapter<Story, StoryAdapter.StoryViewHolder>(StoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StoryViewHolder(
        private val binding: ItemStoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: Story) {
            binding.storyTitle.text = story.title

            // Glide ile resmi yükle
            Glide.with(binding.root.context)
                .load(story.imageUrl)
                .centerCrop()
                .into(binding.storyImage)

            // Hikayeye tıklandığında
            binding.root.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("storyTitle", story.title)
                    putString("storyDescription", story.description)
                    putString("storyImageUrl", story.imageUrl)
                }
                navController.navigate(R.id.storyViewFragment, bundle)
            }
        }
    }
}

class StoryDiffCallback : DiffUtil.ItemCallback<Story>() {
    override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem == newItem
    }
} 