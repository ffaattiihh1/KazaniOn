package com.kazanion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditProfileViewModel : ViewModel() {

    // Dummy Profile Data to be edited
    private val _userName = MutableLiveData<String>("Mevcut Kullanıcı Adı")
    val userName: LiveData<String> get() = _userName

    private val _userEmail = MutableLiveData<String>("mevcut.email@example.com")
    val userEmail: LiveData<String> get() = _userEmail

    // Preferred Availability Data
    private val _preferredDays = MutableLiveData<Set<String>>(emptySet())
    val preferredDays: LiveData<Set<String>> get() = _preferredDays

    private val _preferredStartTime = MutableLiveData<String>("")
    val preferredStartTime: LiveData<String> get() = _preferredStartTime

    private val _preferredEndTime = MutableLiveData<String>("")
    val preferredEndTime: LiveData<String> get() = _preferredEndTime

    init {
        // Simulate loading current profile data
        loadProfileDataForEdit()
    }

    private fun loadProfileDataForEdit() {
        // In a real app, fetch actual user data
        _userName.value = "Güncel Kullanıcı Adı"
        _userEmail.value = "guncel.email@kazanion.com"

        // Load dummy availability settings
        _preferredDays.value = setOf("Pzt", "Çar", "Cum") // Sample preferred days
        _preferredStartTime.value = "09:00" // Sample start time
        _preferredEndTime.value = "17:00" // Sample end time
    }

    fun saveProfile(name: String, email: String, preferredDays: Set<String>, startTime: String, endTime: String) {
        // TODO: Implement actual save logic (e.g., update backend/database)
        // For now, just simulate saving and update ViewModel LiveData
        _userName.value = name
        _userEmail.value = email
        _preferredDays.value = preferredDays
        _preferredStartTime.value = startTime
        _preferredEndTime.value = endTime

        // In a real scenario, you might also notify the ProfileViewModel to refresh
    }

    fun updatePreferredDays(days: Set<String>) {
        _preferredDays.value = days
    }

    fun updatePreferredStartTime(time: String) {
        _preferredStartTime.value = time
    }

    fun updatePreferredEndTime(time: String) {
        _preferredEndTime.value = time
    }

    // TODO: Add function to handle profile image upload/change
} 