package com.kazanion.model

data class Poll(
    val id: Int,
    val title: String,
    val description: String,
    val points: Int,
    val link: String?,
    val locationBased: Boolean,
    val latitude: Double?,
    val longitude: Double?,
    val isActive: Boolean,
    val createdAt: String
) 