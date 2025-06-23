package com.kazanion.model

data class Blog(
    val id: Int,
    val title: String,
    val summary: String,
    val imageUrl: String?, // Optional image URL
    val date: String // Example: "15.07.2024"
    // TODO: Add more fields relevant to a blog post (e.g., content, author)
) 