package com.kazanion.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kazanion.models.Result
import com.kazanion.models.User

class EditProfileViewModel : ViewModel() {
    private val _profileUpdateResult = MutableLiveData<Result<Unit>>()
    val profileUpdateResult: LiveData<Result<Unit>> = _profileUpdateResult

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> = _userData

    init {
        // TODO: Load actual user data
        _userData.value = User(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            firstName = "Test",
            lastName = "User"
        )
    }

    fun updateProfile(
        firstName: String,
        lastName: String,
        email: String,
        username: String
    ) {
        // TODO: Implement actual profile update logic
        if (firstName.isNotEmpty() && email.isNotEmpty() && username.isNotEmpty()) {
            _profileUpdateResult.value = Result.Success(Unit)
        } else {
            _profileUpdateResult.value = Result.Error("Lütfen gerekli alanları doldurun")
        }
    }
} 