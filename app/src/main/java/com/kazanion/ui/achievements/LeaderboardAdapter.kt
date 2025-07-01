package com.kazanion.ui.achievements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kazanion.R
import com.kazanion.model.LeaderboardEntry

class LeaderboardAdapter(
    private val leaderboardEntries: List<LeaderboardEntry>,
    private val currentUserId: Long? = null
) : RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewRank: TextView = itemView.findViewById(R.id.textViewRank)
        val textViewDisplayName: TextView = itemView.findViewById(R.id.textViewDisplayName)
        val textViewPoints: TextView = itemView.findViewById(R.id.textViewPoints)
        val imageBadge: ImageView = itemView.findViewById(R.id.imageBadge)
        val viewHighlight: View? = itemView.findViewById(R.id.viewHighlight)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_leaderboard, parent, false)
        return LeaderboardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val entry = leaderboardEntries[position]
        val context = holder.itemView.context
        
        // Rozet ve rank gösterimi
        when (entry.badge) {
            "gold" -> {
                holder.textViewRank.text = "🥇"
                holder.textViewRank.setTextColor(context.getColor(android.R.color.black))
                holder.textViewRank.setBackgroundResource(R.drawable.gold_circle_background)
                holder.imageBadge.setImageResource(R.drawable.ic_crown_gold)
                holder.imageBadge.visibility = View.VISIBLE
            }
            "silver" -> {
                holder.textViewRank.text = "🥈"
                holder.textViewRank.setTextColor(context.getColor(android.R.color.black))
                holder.textViewRank.setBackgroundResource(R.drawable.silver_circle_background)
                holder.imageBadge.setImageResource(R.drawable.ic_crown_silver)
                holder.imageBadge.visibility = View.VISIBLE
            }
            "bronze" -> {
                holder.textViewRank.text = "🥉"
                holder.textViewRank.setTextColor(context.getColor(android.R.color.black))
                holder.textViewRank.setBackgroundResource(R.drawable.bronze_circle_background)
                holder.imageBadge.setImageResource(R.drawable.ic_crown_bronze)
                holder.imageBadge.visibility = View.VISIBLE
            }
            else -> {
                holder.textViewRank.text = "#${entry.rank}"
                holder.textViewRank.setTextColor(context.getColor(android.R.color.black))
                holder.textViewRank.setBackgroundResource(R.drawable.white_circle_background)
                holder.imageBadge.visibility = View.GONE
            }
        }
        
        // İsim ve puan
        holder.textViewDisplayName.text = entry.displayName
        holder.textViewPoints.text = "${entry.points} puan"
        
        // Eğer mevcut kullanıcı ise vurgula
        if (currentUserId != null && entry.id == currentUserId) {
            holder.viewHighlight?.visibility = View.VISIBLE
            holder.itemView.setBackgroundColor(context.getColor(R.color.light_blue))
        } else {
            holder.viewHighlight?.visibility = View.GONE
            holder.itemView.setBackgroundColor(context.getColor(android.R.color.transparent))
        }
        
        // Animation for top 3
        if (entry.rank <= 3) {
            holder.itemView.alpha = 0f
            holder.itemView.animate()
                .alpha(1f)
                .setDuration(300)
                .setStartDelay((position * 100).toLong())
                .start()
        }
    }

    override fun getItemCount() = leaderboardEntries.size
} 