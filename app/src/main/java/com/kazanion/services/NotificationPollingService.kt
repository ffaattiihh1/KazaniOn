package com.kazanion.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.kazanion.network.ApiService
import com.kazanion.services.NotificationHelper
import kotlinx.coroutines.*

class NotificationPollingService : Service() {
    
    companion object {
        private const val TAG = "NotificationPolling"
        private const val POLL_INTERVAL = 30_000L // 30 saniye
        private const val PREF_LAST_NOTIFICATION_CHECK = "last_notification_check"
        
        fun startPolling(context: Context) {
            val intent = Intent(context, NotificationPollingService::class.java)
            context.startService(intent)
        }
        
        fun stopPolling(context: Context) {
            val intent = Intent(context, NotificationPollingService::class.java)
            context.stopService(intent)
        }
    }
    
    private var pollingJob: Job? = null
    private val apiService by lazy { ApiService.create() }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "🚀 Notification polling service started")
        
        // Notification channel'ını oluştur
        NotificationHelper.createNotificationChannel(this)
        
        startPolling()
        return START_STICKY // Service restart olsun
    }
    
    override fun onDestroy() {
        Log.d(TAG, "🛑 Notification polling service stopped")
        pollingJob?.cancel()
        super.onDestroy()
    }
    
    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                try {
                    checkForNewNotifications()
                    delay(POLL_INTERVAL)
                } catch (e: Exception) {
                    Log.e(TAG, "Error in polling cycle", e)
                    delay(POLL_INTERVAL) // Hata olsa bile devam et
                }
            }
        }
    }
    
    private suspend fun checkForNewNotifications() {
        try {
            val userId = getCurrentUserId()
            if (userId == -1L) {
                Log.d(TAG, "No user logged in, skipping notification check")
                return
            }
            
            Log.d(TAG, "🔍 Checking notifications for user: $userId")
            
            // Backend'den okunmamış bildirimleri getir
            val response = apiService.getUserUnreadNotifications(userId)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                val notifications = responseBody?.get("notifications") as? List<Map<String, Any>> ?: emptyList()
                val unreadCount = (responseBody?.get("unreadCount") as? Number)?.toInt() ?: 0
                
                Log.d(TAG, "📬 Found $unreadCount unread notifications")
                
                if (notifications.isNotEmpty()) {
                    // Her bildirim için local notification göster
                    notifications.forEach { notification ->
                        val title = notification["title"] as? String ?: "KazaniOn"
                        val message = notification["message"] as? String ?: "Yeni bildirim"
                        val notificationId = (notification["id"] as? Number)?.toLong() ?: 0L
                        
                        // Daha önce gösterilmiş mi kontrol et
                        if (!isNotificationShown(notificationId)) {
                            withContext(Dispatchers.Main) {
                                NotificationHelper.showLocalNotification(this@NotificationPollingService, title, message)
                            }
                            markNotificationAsShown(notificationId)
                            Log.d(TAG, "📱 Showed notification: $title")
                        }
                    }
                }
            } else {
                Log.e(TAG, "Failed to get notifications: ${response.code()}")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error checking notifications", e)
        }
    }
    
    private fun getCurrentUserId(): Long {
        val sharedPref = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val userIdString = sharedPref.getString("userId", null)
        return userIdString?.toLongOrNull() ?: -1L
    }
    
    private fun isNotificationShown(notificationId: Long): Boolean {
        val sharedPref = getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("shown_$notificationId", false)
    }
    
    private fun markNotificationAsShown(notificationId: Long) {
        val sharedPref = getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("shown_$notificationId", true)
            apply()
        }
    }
} 