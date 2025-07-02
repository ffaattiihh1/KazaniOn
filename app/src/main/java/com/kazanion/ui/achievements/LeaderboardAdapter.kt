package com.kazanion.ui.achievements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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
        val textViewCoin: TextView = itemView.findViewById(R.id.textViewCoin)
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
        
        // Current user check (KENDİSİ MAVİ, DİĞERLERİ SİYAH)
        val isCurrentUser = currentUserId != null && entry.id == currentUserId
        
        // Profile circle background - KENDİSİ MAVİ, DİĞERLERİ SİYAH
        if (isCurrentUser) {
            // KENDİSİ - MAVİ ARKA PLAN
            holder.textViewRank.setBackgroundResource(R.drawable.circle_background_blue)
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue))
        } else {
            // DİĞERLERİ - SİYAH ARKA PLAN
            holder.textViewRank.setBackgroundResource(R.drawable.circle_background_dark)
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
        
        // Profil resmi göster - Hashtag yerine profil ikonu
        holder.textViewRank.text = "👤"  // Profil ikonu
        
        // Coin sistemi - 1. altın, 2. gümüş, 3. bronz, diğerleri mavi
        when (entry.rank) {
            1 -> holder.textViewCoin.text = "🥇"  // 1. sıra - ALTIN
            2 -> holder.textViewCoin.text = "🥈"  // 2. sıra - GÜMÜŞ  
            3 -> holder.textViewCoin.text = "🥉"  // 3. sıra - BRONZ
            else -> holder.textViewCoin.text = "🔵"  // Diğerleri - MAVİ
        }
        
        holder.imageBadge.visibility = View.GONE
        
        // Display name with rank - "1. Sarah123" formatı
        val username = when {
            !entry.displayName.isNullOrBlank() -> entry.displayName
            !entry.username.isNullOrBlank() -> entry.username  // Backend'ten username kullan
            !entry.firstName.isNullOrBlank() -> entry.firstName // Veya firstName kullan
            else -> "Kullanıcı"  // Son çare
        }
        holder.textViewDisplayName.text = "${entry.rank}. $username"
        val coins = entry.points / 10  // 10 puan = 1 coin
        holder.textViewPoints.text = "$coins"
        
        // Text color - İSİMLER SİYAH OLMALI
        holder.textViewRank.setTextColor(ContextCompat.getColor(context, android.R.color.white))
        holder.textViewDisplayName.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        holder.textViewPoints.setTextColor(ContextCompat.getColor(context, android.R.color.black))
    }

    override fun getItemCount() = leaderboardEntries.size
} 