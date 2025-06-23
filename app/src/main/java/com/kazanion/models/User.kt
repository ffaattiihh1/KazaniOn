package com.kazanion.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName

data class User(
    @DocumentId
    val id: Long = 0,
    val username: String = "",
    val email: String = "",
    @SerializedName("passwordHash")
    val passwordHash: String = "",
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    val points: Int = 0,
    val balance: Double = 0.0
) {
    // Computed property for display name
    val displayName: String
        get() = when {
            !firstName.isNullOrBlank() && !lastName.isNullOrBlank() -> "$firstName $lastName"
            !firstName.isNullOrBlank() -> firstName
            !lastName.isNullOrBlank() -> lastName
            else -> username
        }
    
    // Computed property for masked name (for leaderboard)
    val maskedName: String
        get() = when {
            !firstName.isNullOrBlank() && !lastName.isNullOrBlank() -> {
                val first = firstName.take(2)
                val last = lastName.take(2)
                "$first*** $last***"
            }
            !firstName.isNullOrBlank() -> {
                val first = firstName.take(2)
                "$first***"
            }
            else -> {
                val user = username.take(2)
                "$user***"
            }
        }
} 