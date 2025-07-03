package com.example.demo

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notifications")
class NotificationController(private val notificationService: NotificationService) {
    data class NotificationRequest(val title: String, val message: String)

    @PostMapping
    fun sendNotification(@RequestBody req: NotificationRequest): ResponseEntity<Notification> {
        val notification = notificationService.sendNotification(req.title, req.message)
        return ResponseEntity.ok(notification)
    }

    @GetMapping
    fun getAllNotifications(): ResponseEntity<List<Notification>> =
        ResponseEntity.ok(notificationService.getAllNotifications())
} 