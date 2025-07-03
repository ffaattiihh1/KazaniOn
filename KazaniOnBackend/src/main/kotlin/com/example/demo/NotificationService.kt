package com.example.demo

import org.springframework.stereotype.Service

@Service
class NotificationService(private val notificationRepository: NotificationRepository) {
    fun sendNotification(title: String, message: String): Notification {
        val notification = Notification(title = title, message = message)
        return notificationRepository.save(notification)
    }

    fun getAllNotifications(): List<Notification> = notificationRepository.findAll()
} 