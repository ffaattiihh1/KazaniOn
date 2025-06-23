package com.kazanion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    // TODO: Implement login logic

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    fun login(email: String, password: String) {
        // Simulate login process
        if (email == "test@test.com" && password == "password") { // Dummy credentials
            _loginResult.value = true // Simulate success
        } else {
            _loginResult.value = false // Simulate failure
        }
    }
} 