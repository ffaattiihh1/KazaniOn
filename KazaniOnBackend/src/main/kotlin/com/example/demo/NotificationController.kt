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
} 