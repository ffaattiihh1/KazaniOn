package com.kazanion.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kazanion.models.Result

class LoginViewModel : ViewModel() {
    private val _loginResult = MutableLiveData<Result<Unit>>()
    val loginResult: LiveData<Result<Unit>> = _loginResult

    fun login(email: String, password: String) {
        // TODO: Implement actual login logic
        if (email.isNotEmpty() && password.isNotEmpty()) {
            _loginResult.value = Result.Success(Unit)
        } else {
            _loginResult.value = Result.Error("Email ve şifre boş olamaz")
        }
    }
} 