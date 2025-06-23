package com.kazanion.viewmodel

import androidx.lifecycle.ViewModel
import com.kazanion.model.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.auth.FirebaseAuth
import com.kazanion.services.LogService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {
    private val _loginResult = MutableStateFlow<Result<Unit>>(Result.Loading)
    val loginResult: StateFlow<Result<Unit>> = _loginResult

    private val logService = LogService()
    private val auth = FirebaseAuth.getInstance()

    fun login(email: String, password: String) {
        _loginResult.value = Result.Loading
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null) {
                    if (!user.isEmailVerified) {
                        _loginResult.value = Result.Error("E-posta adresiniz doğrulanmamış. Lütfen e-postanızı kontrol edin.")
                        return@launch
                    }
                    logService.logUserAction(
                        userId = user.uid,
                        action = "login",
                        details = mapOf(
                            "email" to email,
                            "timestamp" to System.currentTimeMillis()
                        ),
                        success = true
                    )
                    _loginResult.value = Result.Success(Unit)
                } else {
                    logService.logError(
                        userId = null,
                        errorType = "login_failed",
                        errorMessage = "Kullanıcı bulunamadı"
                    )
                    _loginResult.value = Result.Error("Kullanıcı bulunamadı")
                }
            } catch (e: Exception) {
                logService.logError(
                    userId = null,
                    errorType = "login_error",
                    errorMessage = e.message ?: "Bilinmeyen hata"
                )
                _loginResult.value = Result.Error("Giriş başarısız: ${e.message}")
            }
        }
    }
} 