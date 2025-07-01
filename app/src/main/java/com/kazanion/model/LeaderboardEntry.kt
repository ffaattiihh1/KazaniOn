package com.kazanion.model

import com.google.gson.annotations.SerializedName

data class LeaderboardEntry(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("rank")
    val rank: Int,
    
    @SerializedName("displayName")
    val displayName: String,
    
    @SerializedName("points")
    val points: Int,
    
    @SerializedName("badge")
    val badge: String? = null
)

data class UserRanking(
    @SerializedName("userId")
    val userId: Long,
    
    @SerializedName("rank")
    val rank: Int,
    
    @SerializedName("displayName")
    val displayName: String,
    
    @SerializedName("points")
    val points: Int,
    
    @SerializedName("badge")
    val badge: String? = null,
    
    @SerializedName("totalUsers")
    val totalUsers: Int
) 