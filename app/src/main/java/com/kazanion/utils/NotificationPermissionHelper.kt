package com.kazanion.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kazanion.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationPermissionHelper(private val context: Context) {

    companion object {
        private const val TAG = "NotificationPermission"
        private const val PERMISSION_REQUEST_CODE = 1001
        const val NOTIFICATION_PERMISSION = "android.permission.POST_NOTIFICATIONS"
        private const val PREF_NOTIFICATION_ENABLED = "notification_enabled"
    }

    fun requestNotificationPermission(activity: Activity, onPermissionResult: (granted: Boolean) -> Unit) {
        try {
            // Android 13+ (API 33) için bildirim izni gerekli
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(context, NOTIFICATION_PERMISSION) 
                    != PackageManager.PERMISSION_GRANTED) {
                    
                    Log.d(TAG, "Requesting notification permission for Android 13+")
                    
                    // İzin iste
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(NOTIFICATION_PERMISSION),
                        PERMISSION_REQUEST_CODE
                    )
                } else {
                    // İzin zaten verilmiş
                    Log.d(TAG, "Notification permission already granted")
                    saveNotificationPermissionGranted()
                    onPermissionResult(true)
                    setupMockFCMToken()
                }
            } else {
                // Android 13 altında izin otomatik verilir
                Log.d(TAG, "Android < 13, notification permission not required")
                saveNotificationPermissionGranted()
                onPermissionResult(true)
                setupMockFCMToken()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting notification permission", e)
            onPermissionResult(false)
        }
    }

    fun handlePermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        onPermissionResult: (granted: Boolean) -> Unit
    ) {
        try {
            if (requestCode == PERMISSION_REQUEST_CODE) {
                val granted = grantResults.isNotEmpty() && 
                             grantResults[0] == PackageManager.PERMISSION_GRANTED
                
                Log.d(TAG, "Notification permission result: $granted")
                
                if (granted) {
                    saveNotificationPermissionGranted()
                    setupMockFCMToken()
                }
                
                onPermissionResult(granted)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling permission result", e)
            onPermissionResult(false)
        }
    }

    private fun setupMockFCMToken() {
        try {
            // Mock FCM token oluştur
            val mockToken = "mock_fcm_token_${System.currentTimeMillis()}"
            Log.d(TAG, "Mock FCM Token: $mockToken")

            // SharedPreferences'e kaydet
            val sharedPref = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("fcm_token", mockToken)
                apply()
            }

            // Backend'e gönder
            sendTokenToServer(mockToken)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up mock FCM token", e)
        }
    }

    private fun sendTokenToServer(token: String) {
        val sharedPref = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val userIdString = sharedPref.getString("userId", null)
        val userId = userIdString?.toLongOrNull() ?: -1L
        
        if (userId != -1L) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val apiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)
                    val response = apiService.updateFcmToken(userId, mapOf("fcmToken" to token))
                    
                    if (response.isSuccessful) {
                        Log.d(TAG, "Mock FCM token successfully sent to server")
                    } else {
                        Log.e(TAG, "Failed to send mock FCM token: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error sending mock FCM token to server", e)
                }
            }
        } else {
            Log.w(TAG, "User ID not found, cannot send mock FCM token")
        }
    }

    fun isNotificationPermissionGranted(): Boolean {
        return try {
            val sharedPref = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
            val userEnabled = sharedPref.getBoolean(PREF_NOTIFICATION_ENABLED, false)
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val systemPermission = ContextCompat.checkSelfPermission(context, NOTIFICATION_PERMISSION) == PackageManager.PERMISSION_GRANTED
                Log.d(TAG, "Notification permission - System: $systemPermission, User enabled: $userEnabled")
                // Her iki koşul da sağlanmalı
                return systemPermission && userEnabled
            } else {
                Log.d(TAG, "Android < 13, checking user enabled flag: $userEnabled")
                // Android 13 altında sadece kullanıcı flag'ini kontrol et
                return userEnabled
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking notification permission", e)
            false
        }
    }
    
    private fun saveNotificationPermissionGranted() {
        try {
            val sharedPref = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean(PREF_NOTIFICATION_ENABLED, true)
                apply()
            }
            Log.d(TAG, "✅ Notification permission flag saved to SharedPreferences")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving notification permission flag", e)
        }
    }

    fun openNotificationSettings(context: Context) {
        try {
            // Android uygulama bildirim ayarlarını aç
            val intent = Intent().apply {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    else -> {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                }
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            
            context.startActivity(intent)
            Log.d(TAG, "Opened notification settings")
        } catch (e: Exception) {
            Log.e(TAG, "Error opening notification settings", e)
            
            // Fallback: Genel ayarları aç
            try {
                val fallbackIntent = Intent(Settings.ACTION_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(fallbackIntent)
            } catch (fallbackError: Exception) {
                Log.e(TAG, "Error opening fallback settings", fallbackError)
            }
        }
    }
} 