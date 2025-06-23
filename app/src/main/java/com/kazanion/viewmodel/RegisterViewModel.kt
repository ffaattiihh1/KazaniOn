package com.kazanion.viewmodel

import androidx.lifecycle.ViewModel
import com.kazanion.model.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kazanion.models.User
import com.kazanion.services.LogService
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel : ViewModel() {
    private val _registerResult = MutableStateFlow<Result<Unit>>(Result.Loading)
    val registerResult: StateFlow<Result<Unit>> = _registerResult

    private val logService = LogService()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun register(firstName: String, lastName: String, email: String, username: String, password: String) {
        _registerResult.value = Result.Loading
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            logService.logError(
                userId = null,
                errorType = "validation_error",
                errorMessage = "Tüm alanlar doldurulmalı"
            )
            _registerResult.value = Result.Error("Tüm alanlar doldurulmalı")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    firebaseUser.sendEmailVerification()
                    val user = User(
                        id = firebaseUser.uid.toLongOrNull() ?: 0L,
                        username = username,
                        email = email,
                        firstName = firstName,
                        lastName = lastName,
                        points = 0,
                        balance = 0.0
                    )
                    
                    db.collection("users").document(user.id.toString()).set(user)
                        .addOnSuccessListener {
                            logService.logUserAction(
                                userId = user.id.toString(),
                                action = "register",
                                details = mapOf(
                                    "email" to email,
                                    "firstName" to (user.firstName ?: ""),
                                    "lastName" to (user.lastName ?: ""),
                                    "username" to username
                                ),
                                success = true
                            )
                            _registerResult.value = Result.Error("Kayıt başarılı! Lütfen e-posta adresinizi doğrulayın.")
                        }
                        .addOnFailureListener { e ->
                            logService.logError(
                                userId = user.id.toString(),
                                errorType = "firestore_error",
                                errorMessage = "Kullanıcı Firestore'a eklenemedi: ${e.message}",
                                stackTrace = e.stackTraceToString()
                            )
                            _registerResult.value = Result.Error("Firestore'a yazılamadı: ${e.message}")
                        }
                } else {
                    logService.logError(
                        userId = null,
                        errorType = "auth_error",
                        errorMessage = "Kullanıcı oluşturulamadı"
                    )
                    _registerResult.value = Result.Error("Kullanıcı oluşturulamadı")
                }
            } catch (e: Exception) {
                logService.logError(
                    userId = null,
                    errorType = "registration_error",
                    errorMessage = e.message ?: "Bilinmeyen hata",
                    stackTrace = e.stackTraceToString()
                )
                _registerResult.value = Result.Error("Kayıt başarısız: ${e.message}")
            }
        }
    }
} 