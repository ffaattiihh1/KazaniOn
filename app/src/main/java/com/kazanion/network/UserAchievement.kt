package com.kazanion.network

data class UserAchievement(
    val id: Long,
    val title: String,
    val description: String,
    val iconName: String,
    val pointsReward: Int,
    val earnedAt: String,
    val pointsEarned: Int
) 