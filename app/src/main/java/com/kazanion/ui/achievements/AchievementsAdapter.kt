package com.kazanion.ui.achievements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kazanion.R
import com.kazanion.model.Achievement

class AchievementsAdapter(
    private val achievementList: List<Achievement>
) : RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder>() {

    class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewIcon: ImageView = itemView.findViewById(R.id.imageViewAchievementIcon)
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewAchievementTitle)
        val textViewDescription: TextView = itemView.findViewById(R.id.textViewAchievementDescription)
        val textViewStatus: TextView = itemView.findViewById(R.id.textViewAchievementStatus)
        val textViewPoints: TextView = itemView.findViewById(R.id.textViewAchievementPoints)
        val imageViewCheckmark: ImageView = itemView.findViewById(R.id.imageViewCheckmark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_achievement, parent, false)
        return AchievementViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievementList[position]

        // Set icon based on achievement type
        val iconResId = when (achievement.iconName) {
            "ic_first_poll" -> R.drawable.ic_polls
            "ic_poll_lover" -> R.drawable.ic_polls
            "ic_poll_master" -> R.drawable.ic_polls
            "ic_social" -> R.drawable.ic_friends
            "ic_explorer" -> R.drawable.ic_explorer
            "ic_fast_start" -> R.drawable.ic_wallet
            "ic_daily_active" -> R.drawable.ic_calendar
            "ic_location_explorer" -> R.drawable.ic_location
            "ic_point_hunter" -> R.drawable.ic_money
            else -> R.drawable.ic_achievements
        }
        
        holder.imageViewIcon.setImageResource(iconResId)
        holder.textViewTitle.text = achievement.title
        holder.textViewDescription.text = achievement.description
        holder.textViewPoints.text = "${achievement.pointsReward / 10} Coin"  // Sadece coin göster

        // Set status and visual appearance based on earned status - MAVİ TİK
        if (achievement.isEarned) {
            holder.textViewStatus.text = "✓ Tamamlandı"
            holder.textViewStatus.setTextColor(holder.itemView.context.getColor(R.color.kazanion_blue))
            holder.imageViewIcon.alpha = 1.0f
            holder.textViewTitle.alpha = 1.0f
            holder.textViewDescription.alpha = 1.0f
            holder.imageViewCheckmark.visibility = View.VISIBLE
            holder.imageViewCheckmark.setImageResource(R.drawable.ic_check_circle)
            holder.imageViewCheckmark.setColorFilter(holder.itemView.context.getColor(R.color.kazanion_blue))  // MAVİ TİK
            
            // Add earned date if available
            achievement.earnedAt?.let { date ->
                holder.textViewStatus.text = "✓ Tamamlandı - $date"
            }
        } else {
            holder.textViewStatus.text = "Tamamlanmadı"
            holder.textViewStatus.setTextColor(holder.itemView.context.getColor(R.color.secondary_dark))
            holder.imageViewIcon.alpha = 0.5f
            holder.textViewTitle.alpha = 0.7f
            holder.textViewDescription.alpha = 0.7f
            holder.imageViewCheckmark.visibility = View.GONE
        }
    }

    override fun getItemCount() = achievementList.size
} 