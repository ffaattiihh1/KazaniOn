package com.kazanion.services

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import java.util.Date

class LogService {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "LogService"

    fun logUserAction(
        userId: String,
        action: String,
        details: Map<String, Any> = mapOf(),
        success: Boolean = true
    ) {
        val logData = hashMapOf(
            "userId" to userId,
            "action" to action,
            "details" to details,
            "success" to success,
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("logs")
            .add(logData)
            .addOnSuccessListener {
                Log.d(TAG, "Log başarıyla kaydedildi: $action")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Log kaydedilirken hata oluştu: ${e.message}")
            }
    }

    fun logError(
        userId: String?,
        errorType: String,
        errorMessage: String,
        stackTrace: String? = null
    ) {
        val logData = hashMapOf(
            "userId" to (userId ?: "unknown"),
            "errorType" to errorType,
            "errorMessage" to errorMessage,
            "stackTrace" to (stackTrace ?: ""),
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("error_logs")
            .add(logData)
            .addOnSuccessListener {
                Log.d(TAG, "Hata logu başarıyla kaydedildi: $errorType")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Hata logu kaydedilirken hata oluştu: ${e.message}")
            }
    }

    fun logSurveyAction(
        userId: String,
        surveyId: String,
        action: String,
        details: Map<String, Any> = mapOf()
    ) {
        val logData = hashMapOf(
            "userId" to userId,
            "surveyId" to surveyId,
            "action" to action,
            "details" to details,
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("survey_logs")
            .add(logData)
            .addOnSuccessListener {
                Log.d(TAG, "Anket logu başarıyla kaydedildi: $action")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Anket logu kaydedilirken hata oluştu: ${e.message}")
            }
    }
} 