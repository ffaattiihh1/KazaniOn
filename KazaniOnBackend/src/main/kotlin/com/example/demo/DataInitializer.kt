package com.example.demo

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext

@Component
class DataInitializer {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @EventListener(ApplicationReadyEvent::class)
    @Transactional
    fun onApplicationReady() {
        try {
            println("🔧 DataInitializer started - but skipping database modifications")
            println("✅ Application ready without database changes")
        } catch (e: Exception) {
            println("❌ DataInitializer error: ${e.message}")
        }
    }
} 