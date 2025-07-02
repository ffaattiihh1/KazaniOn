package com.kazanion.model

data class Poll(
    val id: Int,
    val title: String,
    val description: String,
    val points: Int = 0,       // Backend'den gelen puan değeri
    val price: Double? = null, // Admin panel için (opsiyonel)
    val link: String?,
    val locationBased: Boolean,
    val latitude: Double?,
    val longitude: Double?,
    val isActive: Boolean,
    val createdAt: String
) 