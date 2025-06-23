package com.kazanion.network

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class LocationPoll(
    val id: Long,
    val title: String,
    val description: String,
    val points: Int,
    val locationBased: Boolean,
    val latitude: Double?,
    val longitude: Double?,
    val isActive: Boolean,
    val createdAt: String
) : Parcelable 