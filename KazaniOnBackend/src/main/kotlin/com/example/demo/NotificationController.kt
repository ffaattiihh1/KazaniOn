package com.example.demo

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = ["*"])
class NotificationController(private val notificationService: NotificationService) {
    data class NotificationRequest(val title: String, val message: String)
    data class SendNotificationRequest(val title: String, val message: String, val userId: Long?)

    @PostMapping
    fun sendNotification(@RequestBody req: NotificationRequest): ResponseEntity<Notification> {
        val notification = notificationService.sendNotification(req.title, req.message)
        return ResponseEntity.ok(notification)
    }

    @PostMapping("/send")
    fun sendNotificationToUser(@RequestBody req: SendNotificationRequest): ResponseEntity<Map<String, Any>> {
        try {
            val notification = if (req.userId != null) {
                // Send to specific user
                notificationService.sendNotificationToUser(req.userId, req.title, req.message)
            } else {
                // Send to all users (global notification)
                notificationService.sendNotification(req.title, req.message)
            }
            
            return ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Notification sent successfully!",
                "notification" to notification
            ))
        } catch (e: Exception) {
            return ResponseEntity.ok(mapOf(
                "success" to false,
                "message" to "Failed to send notification: ${e.message}"
            ))
        }
    }

    @PostMapping("/test")
    fun sendTestNotification(): ResponseEntity<Map<String, Any>> {
        try {
            val notification = notificationService.sendNotification(
                "Test Bildirimi", 
                "Bu bir test bildirimidir. Firebase sistemi çalışıyor! 🚀"
            )
            return ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Test notification sent successfully!",
                "notification" to notification
            ))
        } catch (e: Exception) {
            return ResponseEntity.ok(mapOf(
                "success" to false,
                "message" to "Failed to send notification: ${e.message}"
            ))
        }
    }

    @GetMapping
    fun getAllNotifications(): ResponseEntity<List<Notification>> =
        ResponseEntity.ok(notificationService.getAllNotifications())

    @GetMapping("/user/{userId}/unread")
    fun getUserUnreadNotifications(@PathVariable userId: Long): ResponseEntity<Map<String, Any>> {
        try {
            // For now, return all notifications as unread (since we don't have user-specific tracking yet)
            // This is a simplified implementation
            val allNotifications = notificationService.getAllNotifications()
            
            // Simulate unread notifications (in a real app, you'd track read status per user)
            val notifications = allNotifications.map { notification ->
                mapOf(
                    "id" to notification.id,
                    "title" to notification.title,
                    "message" to notification.message,
                    "createdAt" to notification.createdAt.toString(),
                    "isRead" to false,
                    "isGlobal" to true
                )
            }
            
            return ResponseEntity.ok(mapOf(
                "success" to true,
                "notifications" to notifications,
                "unreadCount" to notifications.size
            ))
        } catch (e: Exception) {
            return ResponseEntity.ok(mapOf(
                "success" to false,
                "message" to "Failed to get notifications: ${e.message}",
                "notifications" to emptyList<Map<String, Any>>(),
                "unreadCount" to 0
            ))
        }
    }
} 