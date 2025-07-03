package com.example.demo

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext

@Component
class DataInitializer : CommandLineRunner {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional
    override fun run(vararg args: String?) {
        try {
            println("🔧 Fixing null price values in poll table...")
            
            // Update null price values to 0.0
            val updateQuery = entityManager.createNativeQuery("UPDATE poll SET price = 0.0 WHERE price IS NULL")
            val updatedRows = updateQuery.executeUpdate()
            
            println("✅ Updated $updatedRows rows with null price values")
            
            // Also create some sample polls if the table is empty
            val countQuery = entityManager.createNativeQuery("SELECT COUNT(*) FROM poll")
            val count = (countQuery.singleResult as Number).toLong()
            
            if (count == 0L) {
                println("📊 Creating sample polls...")
                createSamplePolls()
            } else {
                println("📊 Poll table already has $count polls")
            }
            
        } catch (e: Exception) {
            println("⚠️ Error during data initialization: ${e.message}")
            // Don't throw the exception to prevent app startup failure
        }
    }
    
    private fun createSamplePolls() {
        val samplePolls = listOf(
            "INSERT INTO poll (title, description, price, points, is_active, created_at) VALUES " +
                    "('Sample Poll 1', 'This is a sample poll', 0.0, 10, true, NOW())",
            
            "INSERT INTO poll (title, description, price, points, is_active, created_at, link_url) VALUES " +
                    "('Link Poll', 'Complete this online survey', 0.0, 15, true, NOW(), 'https://example.com/survey')",
            
            "INSERT INTO poll (title, description, price, points, is_active, created_at, latitude, longitude, radius) VALUES " +
                    "('Location Poll', 'Complete this poll at the specified location', 0.0, 20, true, NOW(), 40.7128, -74.0060, 500.0)"
        )
        
        samplePolls.forEach { sql ->
            try {
                entityManager.createNativeQuery(sql).executeUpdate()
                println("✅ Sample poll created")
            } catch (e: Exception) {
                println("⚠️ Error creating sample poll: ${e.message}")
            }
        }
    }
} 