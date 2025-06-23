package com.kazanion.model

import com.google.gson.annotations.SerializedName

data class Achievement(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("iconName")
    val iconName: String,
    
    @SerializedName("pointsReward")
    val pointsReward: Int,
    
    @SerializedName("requirementType")
    val requirementType: String,
    
    @SerializedName("requirementValue")
    val requirementValue: Int,
    
    var isEarned: Boolean = false,
    
    @SerializedName("earnedAt")
    val earnedAt: String? = null,
    
    @SerializedName("pointsEarned")
    val pointsEarned: Int? = null
) 