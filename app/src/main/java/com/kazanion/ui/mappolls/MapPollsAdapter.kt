package com.kazanion.ui.mappolls

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kazanion.databinding.ListItemMapPollBinding
import com.kazanion.network.LocationPoll
import kotlin.math.*

class MapPollsAdapter(
    private var polls: List<LocationPoll>,
    private var userLat: Double? = null,
    private var userLng: Double? = null,
    private val onItemClick: ((LocationPoll, String, String) -> Unit)? = null
) : RecyclerView.Adapter<MapPollsAdapter.PollViewHolder>() {

    // Constructor for HomeFragment
    constructor(polls: List<LocationPoll>, onItemClick: (LocationPoll, String, String) -> Unit) : this(polls, null, null, onItemClick)

    class PollViewHolder(private val binding: ListItemMapPollBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(poll: LocationPoll, userLat: Double?, userLng: Double?, onItemClick: ((LocationPoll, String, String) -> Unit)?) {
            binding.pollTitle.text = poll.title
            binding.pollReward.text = "${poll.points} TL"
            
            val distance = if (userLat != null && userLng != null && poll.latitude != null && poll.longitude != null) {
                val dist = haversine(userLat, userLng, poll.latitude, poll.longitude)
                String.format("%.1f km uzaklıkta", dist)
            } else {
                "-"
            }
            
            binding.pollDistance.text = distance
            binding.root.setOnClickListener { 
                onItemClick?.invoke(poll, distance, poll.description)
            }
        }
        
        private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val R = 6371e3 // metre
            val phi1 = lat1 * Math.PI / 180
            val phi2 = lat2 * Math.PI / 180
            val deltaPhi = (lat2 - lat1) * Math.PI / 180
            val deltaLambda = (lon2 - lon1) * Math.PI / 180
            val a = sin(deltaPhi/2) * sin(deltaPhi/2) + cos(phi1) * cos(phi2) * sin(deltaLambda/2) * sin(deltaLambda/2)
            val c = 2 * atan2(sqrt(a), sqrt(1-a))
            return R * c / 1000 // km
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollViewHolder {
        val binding = ListItemMapPollBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PollViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PollViewHolder, position: Int) {
        val poll = polls[position]
        holder.bind(poll, userLat, userLng, onItemClick)
    }

    override fun getItemCount(): Int = polls.size

    fun updateData(newPolls: List<LocationPoll>, userLat: Double?, userLng: Double?) {
        this.polls = newPolls
        this.userLat = userLat
        this.userLng = userLng
        notifyDataSetChanged()
    }
} 