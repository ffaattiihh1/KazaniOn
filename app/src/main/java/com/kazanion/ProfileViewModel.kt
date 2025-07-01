package com.kazanion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.kazanion.models.User
import com.kazanion.network.ApiService

class ProfileViewModel : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val apiService = ApiService.create()

    fun initializeWithUsername(username: String) {
        loadUserProfile(username)
    }

    fun loadUserProfile(username: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                android.util.Log.d("ProfileViewModel", "=== LOADING USER PROFILE ===")
                android.util.Log.d("ProfileViewModel", "Username: $username")
                android.util.Log.d("ProfileViewModel", "API call: getUserBalance($username)")
                
                val userBalance = apiService.getUserBalance(username)
                android.util.Log.d("ProfileViewModel", "API response: $userBalance")
                
                val user = User(
                    id = userBalance.id,
                    username = userBalance.username,
                    email = userBalance.email ?: "",
                    firstName = userBalance.firstName ?: "Kullanıcı",
                    lastName = userBalance.lastName ?: "",
                    points = userBalance.points,
                    balance = userBalance.balance
                )
                android.util.Log.d("ProfileViewModel", "User object created: $user")
                _user.value = user
                android.util.Log.d("ProfileViewModel", "=== USER PROFILE LOADED SUCCESSFULLY ===")
            } catch (e: Exception) {
                android.util.Log.e("ProfileViewModel", "API error: ${e.message}", e)
                _error.value = "Kullanıcı bilgileri yüklenirken hata oluştu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getCurrentUser(): User? {
        return _user.value
    }
} 