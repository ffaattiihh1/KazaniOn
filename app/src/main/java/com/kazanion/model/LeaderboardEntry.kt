package com.kazanion.model

import com.google.gson.annotations.SerializedName

data class LeaderboardEntry(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("rank")
    val rank: Int = 0,  // Backend'te olmayabilir, default değer
    
    @SerializedName("displayName")
    val displayName: String?,  // NULL CONTROL için nullable yapıldı
    
    @SerializedName("username")
    val username: String? = null,  // Backend'ten username field'ını al
    
    @SerializedName("firstName")
    val firstName: String? = null,  // Backend'ten firstName field'ını al
    
    @SerializedName("lastName")
    val lastName: String? = null,   // Backend'ten lastName field'ını al
    
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
    val displayName: String?,  // NULL CONTROL için nullable yapıldı
    
    @SerializedName("points")
    val points: Int,
    
    @SerializedName("badge")
    val badge: String? = null,
    
    @SerializedName("totalUsers")
    val totalUsers: Int
) 