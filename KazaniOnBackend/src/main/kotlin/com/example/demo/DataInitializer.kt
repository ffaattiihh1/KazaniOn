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
            println("🔧 Checking and fixing null price values in poll table...")
            
            // First check if poll table exists and has null values
            val checkQuery = entityManager.createNativeQuery("SELECT COUNT(*) FROM poll WHERE price IS NULL")
            val nullCount = (checkQuery.singleResult as Number).toLong()
            
            if (nullCount > 0) {
                println("⚠️ Found $nullCount polls with null price values, fixing...")
                
                // Update null price values to 0.0
                val updateQuery = entityManager.createNativeQuery("UPDATE poll SET price = 0.0 WHERE price IS NULL")
                val updatedRows = updateQuery.executeUpdate()
                
                println("✅ Successfully updated $updatedRows polls with default price value (0.0)")
            } else {
                println("✅ No null price values found in poll table")
            }
            
            // Also ensure price column is NOT NULL for future entries
            try {
                val alterQuery = entityManager.createNativeQuery("ALTER TABLE poll ALTER COLUMN price SET NOT NULL")
                alterQuery.executeUpdate()
                println("✅ Set price column to NOT NULL")
            } catch (e: Exception) {
                println("ℹ️ Price column constraint already exists or modification not needed: ${e.message}")
            }
            
        } catch (e: Exception) {
            println("❌ Error fixing poll price values: ${e.message}")
            // Don't throw the exception to prevent application startup failure
        }
    }
} 