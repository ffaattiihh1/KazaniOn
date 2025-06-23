package com.kazanion.ui.blog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kazanion.R
import com.kazanion.model.Blog

class BlogAdapter(private val blogList: List<Blog>) : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    class BlogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewBlogTitle)
        val textViewSummary: TextView = itemView.findViewById(R.id.textViewBlogSummary)
        val imageViewBlog: ImageView = itemView.findViewById(R.id.imageViewBlog)
        val textViewDate: TextView = itemView.findViewById(R.id.textViewBlogDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_blog, parent, false)
        return BlogViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val currentBlog = blogList[position]
        holder.textViewTitle.text = currentBlog.title
        holder.textViewSummary.text = currentBlog.summary
        holder.textViewDate.text = currentBlog.date

        if (currentBlog.imageUrl != null && currentBlog.imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(currentBlog.imageUrl)
                .placeholder(R.drawable.ic_placeholder) // Optional placeholder
                .error(R.drawable.ic_placeholder) // Optional error image
                .into(holder.imageViewBlog)
            holder.imageViewBlog.visibility = View.VISIBLE
        } else {
            holder.imageViewBlog.visibility = View.GONE
        }

        // TODO: Add click listener for each blog item if needed
        holder.itemView.setOnClickListener { 
            // Handle blog item click
        }
    }

    override fun getItemCount() = blogList.size
} 