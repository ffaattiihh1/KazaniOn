package com.kazanion.model

import android.net.Uri

data class CircleItem(
    val id: Int,
    val title: String,
    val iconResId: Int,
    val colorResId: Int,
    var isSeen: Boolean = false,
    val isProfileItem: Boolean = false,
    var profileImageUri: Uri? = null
) 