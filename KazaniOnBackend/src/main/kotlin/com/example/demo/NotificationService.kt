package com.example.demo

import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository
) {
    fun sendNotification(title: String, message: String): Notification {
        println("📱 PostgreSQL Global Notification saved: $title - $message")
        val notification = Notification(title = title, message = message)
        val saved = notificationRepository.save(notification)
        
        val userCount = userRepository.count()
        println("✅ Global notification will be visible to $userCount users")
        
        return saved
    }
    
    fun sendNotificationToUser(userId: Long, title: String, message: String): Notification {
        val user = userRepository.findById(userId).orElseThrow { 
            RuntimeException("User with ID $userId not found") 
        }
        
        println("📱 PostgreSQL Notification saved for user ${user.username}: $title - $message")
        val notification = Notification(title = title, message = message)
        return notificationRepository.save(notification)
    }

    fun getAllNotifications(): List<Notification> = notificationRepository.findAll()
} 