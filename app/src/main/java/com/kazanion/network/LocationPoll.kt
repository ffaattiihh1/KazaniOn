package com.kazanion.network

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class LocationPoll(
    val id: Long,
    val title: String,
    val description: String,
    val points: Int = 0,       // Backend'den gelen puan değeri
    val price: Double? = null, // Admin panel için (opsiyonel)
    val link: String?,
    val latitude: Double?,
    val longitude: Double?,
    val isActive: Boolean,
    val createdAt: String,
    val locationBased: Boolean = true
) : Parcelable 