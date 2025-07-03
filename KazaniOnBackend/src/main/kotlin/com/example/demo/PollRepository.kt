package com.example.demo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PollRepository : JpaRepository<Poll, Long> {
    
    @Query("SELECT p FROM Poll p WHERE p.isActive = true")
    fun findActivePolls(): List<Poll>
    
    @Query("SELECT p FROM Poll p WHERE p.isActive = true AND p.linkUrl IS NOT NULL")
    fun findActiveLinkPolls(): List<Poll>
    
    @Query("SELECT p FROM Poll p WHERE p.isActive = true AND p.latitude IS NOT NULL AND p.longitude IS NOT NULL")
    fun findLocationBasedPolls(): List<Poll>
} 