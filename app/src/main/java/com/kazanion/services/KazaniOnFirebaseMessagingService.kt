package com.kazanion.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.kazanion.MainActivity
import com.kazanion.R
import com.kazanion.network.ApiService
import com.kazanion.utils.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Şimdilik Firebase service'i comment out ediyoruz
// Firebase olmadan test edebilmek için

/*
class KazaniOnFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "kazanion_notifications"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.title ?: "KazaniOn", it.body ?: "")
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        
        // FCM token'ını backend'e gönder
        sendTokenToServer(token)
        
        // SharedPreferences'e kaydet
        val sharedPref = getSharedPreferences("KazaniOnPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("fcm_token", token)
            apply()
        }
    }

    private fun sendTokenToServer(token: String) {
        val sharedPref = getSharedPreferences("KazaniOnPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getLong("user_id", -1)
        
        if (userId != -1L) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val apiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)
                    val response = apiService.updateFcmToken(userId, mapOf("fcmToken" to token))
                    
                    if (response.isSuccessful) {
                        Log.d(TAG, "FCM token successfully sent to server")
                    } else {
                        Log.e(TAG, "Failed to send FCM token: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error sending FCM token to server", e)
                }
            }
        }
    }

    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "KazaniOn Bildirimleri"
            val descriptionText = "KazaniOn uygulamasından gelen bildirimler"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
*/

// Demo amaçlı gerçek local notification helper
class NotificationHelper {
    companion object {
        private const val TAG = "NotificationHelper"
        private const val CHANNEL_ID = "kazanion_notifications"
        private const val NOTIFICATION_ID_BASE = 1000
        
        fun createNotificationChannel(context: Context) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channel = android.app.NotificationChannel(
                    CHANNEL_ID,
                    "KazaniOn Bildirimleri",
                    android.app.NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "KazaniOn uygulamasından gelen bildirimler"
                    enableLights(true)
                    lightColor = android.graphics.Color.BLUE
                    enableVibration(true)
                }
                
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                notificationManager.createNotificationChannel(channel)
                Log.d(TAG, "✅ Notification channel created: $CHANNEL_ID")
            }
        }
        
        fun showLocalNotification(context: Context, title: String, message: String) {
            Log.d(TAG, "📱 Showing local notification: $title - $message")
            
            createNotificationChannel(context)
            
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )

            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Bu icon yoksa ic_launcher kullan
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Unique notification ID for each notification
            val notificationId = NOTIFICATION_ID_BASE + (System.currentTimeMillis() % 1000).toInt()
            notificationManager.notify(notificationId, notificationBuilder.build())
            
            Log.d(TAG, "✅ Local notification displayed with ID: $notificationId")
        }

    }
} 