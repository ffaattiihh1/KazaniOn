package com.kazanion.ui.achievements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kazanion.R
import com.kazanion.model.LeaderboardEntry

class LeaderboardAdapter(
    private val leaderboardEntries: List<LeaderboardEntry>
) : RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewRank: TextView = itemView.findViewById(R.id.textViewRank)
        val textViewUsername: TextView = itemView.findViewById(R.id.textViewUsername)
        val textViewPoints: TextView = itemView.findViewById(R.id.textViewPoints)
        val textViewCoins: TextView = itemView.findViewById(R.id.textViewBalance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_leaderboard, parent, false)
        return LeaderboardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val entry = leaderboardEntries[position]
        
        holder.textViewRank.text = "#${entry.rank}"
        holder.textViewUsername.text = entry.maskedName
        holder.textViewPoints.text = "${entry.points} Puan"
        holder.textViewCoins.text = "${entry.coins} Coin (${entry.coins * 0.1} TL)"
        
        // Highlight top 3 positions
        when (entry.rank) {
            1 -> {
                holder.textViewRank.setTextColor(holder.itemView.context.getColor(R.color.gold))
                holder.textViewRank.text = "🥇"
            }
            2 -> {
                holder.textViewRank.setTextColor(holder.itemView.context.getColor(R.color.silver))
                holder.textViewRank.text = "🥈"
            }
            3 -> {
                holder.textViewRank.setTextColor(holder.itemView.context.getColor(R.color.bronze))
                holder.textViewRank.text = "🥉"
            }
            else -> {
                holder.textViewRank.setTextColor(holder.itemView.context.getColor(R.color.black))
            }
        }
    }

    override fun getItemCount() = leaderboardEntries.size
} 