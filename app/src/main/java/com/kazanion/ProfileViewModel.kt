package com.kazanion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.kazanion.models.User

class ProfileViewModel : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // Dummy User Data
    private val _userName = MutableLiveData<String>("Test User")
    val userName: LiveData<String> get() = _userName

    private val _userEmail = MutableLiveData<String>("test@example.com")
    val userEmail: LiveData<String> get() = _userEmail

    private val _completedPollsCount = MutableLiveData<Int>(0)
    val completedPollsCount: LiveData<Int> get() = _completedPollsCount

    private val _totalPoints = MutableLiveData<Int>(0)
    val totalPoints: LiveData<Int> get() = _totalPoints

    private val _giftVouchersCount = MutableLiveData<Int>(0)
    val giftVouchersCount: LiveData<Int> get() = _giftVouchersCount

    // Dummy Settings Data
    private val _notificationsEnabled = MutableLiveData<Boolean>(true)
    val notificationsEnabled: LiveData<Boolean> get() = _notificationsEnabled

    private val _selectedLanguage = MutableLiveData<String>("Türkçe")
    val selectedLanguage: LiveData<String> get() = _selectedLanguage

    init {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            loadUser(userId)
        }
    }

    private fun loadProfileData() {
        // Artık kullanılmıyor, Firestore'dan veri çekiliyor.
    }

    fun loadUser(userId: String) {
        viewModelScope.launch {
            try {
                val doc = FirebaseFirestore.getInstance().collection("users").document(userId).get().await()
                val user = doc.toObject(User::class.java)
                _user.value = user
            } catch (e: Exception) {
                _error.value = "Kullanıcı bilgileri yüklenirken hata oluştu: ${e.message}"
            }
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        // TODO: Save setting to preferences or backend
    }

    fun updateSelectedLanguage(language: String) {
        _selectedLanguage.value = language
        // TODO: Save setting and potentially restart/recreate activity for language change
    }

    fun editProfile() {
        // TODO: Handle navigation to edit profile screen
    }

    fun navigateToFeature(feature: String) {
        // TODO: Handle navigation to specific feature screen (Konum, Duygu, Cüzdan, etc.)
    }

    fun navigateToPrivacySettings() {
        // TODO: Handle navigation to privacy settings screen
    }
} 