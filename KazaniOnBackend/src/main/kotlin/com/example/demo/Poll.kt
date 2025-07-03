package com.example.demo

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "poll")
data class Poll(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false)
    val title: String,
    
    @Column(nullable = false)
    val description: String,
    
    @Column(nullable = true) // Allow null values for now to fix the database error
    val price: Double? = null,
    
    @Column(nullable = false)
    val points: Int = 0,
    
    @Column(nullable = false)
    val isActive: Boolean = true,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val latitude: Double? = null,
    val longitude: Double? = null,
    val radius: Double? = null,
    val linkUrl: String? = null
) 