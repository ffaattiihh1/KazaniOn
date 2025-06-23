package com.kazanion.model

import com.google.gson.annotations.SerializedName

data class LeaderboardEntry(
    @SerializedName("rank")
    val rank: Int,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("maskedName")
    val maskedName: String,
    
    @SerializedName("firstName")
    val firstName: String?,
    
    @SerializedName("lastName")
    val lastName: String?,
    
    @SerializedName("points")
    val points: Int,
    
    @SerializedName("coins")
    val coins: Double,
    
    @SerializedName("balance")
    val balance: Double
) 