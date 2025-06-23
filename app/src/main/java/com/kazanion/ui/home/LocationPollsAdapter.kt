package com.kazanion.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kazanion.R
import com.kazanion.network.LocationPoll

class LocationPollsAdapter(
    private var pollList: List<LocationPoll>,
    private val onItemClick: (LocationPoll, String, String) -> Unit,
    private var userLat: Double? = null,
    private var userLng: Double? = null
) : RecyclerView.Adapter<LocationPollsAdapter.LocationPollViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationPollViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location_poll, parent, false)
        return LocationPollViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationPollViewHolder, position: Int) {
        val poll = pollList[position]
        val distance = calculateDistance(userLat, userLng, poll.latitude, poll.longitude)
        val distanceText = if (distance != null) String.format("%.1f km uzaklıkta", distance) else ""
        val rewardText = "${poll.points} Puan"
        val address = poll.description
        holder.bind(poll, rewardText, distanceText)
        holder.itemView.setOnClickListener { onItemClick(poll, distanceText, address) }
    }

    override fun getItemCount() = pollList.size

    fun updateData(newPolls: List<LocationPoll>, lat: Double?, lng: Double?) {
        pollList = newPolls
        userLat = lat
        userLng = lng
        notifyDataSetChanged()
    }

    class LocationPollViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.pollTitle)
        private val reward: TextView = itemView.findViewById(R.id.pollReward)
        private val distance: TextView = itemView.findViewById(R.id.pollDistance)
        private val description: TextView = itemView.findViewById(R.id.pollDescription)
        
        fun bind(poll: LocationPoll, rewardText: String, distanceText: String) {
            title.text = poll.title
            reward.text = rewardText
            distance.text = distanceText
            
            // Açıklama varsa göster
            if (!poll.description.isNullOrEmpty()) {
                description.text = poll.description
                description.visibility = View.VISIBLE
            } else {
                description.visibility = View.GONE
            }
        }
    }

    private fun calculateDistance(
        userLat: Double?,
        userLng: Double?,
        pollLat: Double?,
        pollLng: Double?
    ): Double? {
        if (userLat == null || userLng == null || pollLat == null || pollLng == null) return null
        val earthRadius = 6371.0 // km
        val dLat = Math.toRadians(pollLat - userLat)
        val dLng = Math.toRadians(pollLng - userLng)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(pollLat)) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }
} 