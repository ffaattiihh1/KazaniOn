package com.kazanion.models

import com.google.gson.annotations.SerializedName

data class User(
    val id: Long = 0,
    val username: String = "",
    val email: String = "",
    @SerializedName("passwordHash")
    val passwordHash: String = "",
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    @SerializedName("birthDate")
    val birthDate: String? = null,
    @SerializedName("publicId")
    val publicId: String? = null,  // 6 haneli public ID
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
    
    // Computed property for first name only (for homepage)
    val firstNameOnly: String
        get() = when {
            !firstName.isNullOrBlank() -> firstName
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