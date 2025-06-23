package com.kazanion.network

import com.google.gson.annotations.SerializedName

data class UserBalance(
    val id: Long,
    val username: String,
    val firstName: String?,
    val lastName: String?,
    val email: String,
    val points: Int,
    val balance: Double
) 