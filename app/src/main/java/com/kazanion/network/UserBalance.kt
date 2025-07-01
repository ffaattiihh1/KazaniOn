package com.kazanion.network

import com.google.gson.annotations.SerializedName

data class UserBalance(
    val id: Long,
    val username: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val points: Int,
    val balance: Double
) 