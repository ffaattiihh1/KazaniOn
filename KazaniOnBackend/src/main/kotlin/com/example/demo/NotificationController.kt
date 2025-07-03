package com.example.demo

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = ["*"])
class NotificationController(private val notificationService: NotificationService) {
    data class NotificationRequest(val title: String, val message: String)

    @PostMapping
    fun sendNotification(@RequestBody req: NotificationRequest): ResponseEntity<Notification> {
        val notification = notificationService.sendNotification(req.title, req.message)
        return ResponseEntity.ok(notification)
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