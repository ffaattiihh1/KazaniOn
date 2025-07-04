package com.example.demo

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "poll")
data class Poll(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false)
    val title: String = "",
    
    @Column(nullable = false)
    val description: String = "",
    
    @Column(nullable = true) // Make nullable to handle existing null values
    val price: Double? = null,
    
    @Column(nullable = true)
    val points: Int? = 0,
    
    @Column(nullable = false)
    val isActive: Boolean = true,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val latitude: Double? = null,
    val longitude: Double? = null,
    val radius: Double? = null,
    val linkUrl: String? = null,
    val imageUrl: String? = null,
    val categoryId: Long? = null,
    val requiredLevel: Int? = 1,
    val maxCompletions: Int? = null,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null
) 