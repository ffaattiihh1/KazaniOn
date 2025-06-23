package com.kazanion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InviteViewModel : ViewModel() {

    private val _referralCode = MutableLiveData<String>("KAZANI123") // Sample referral code
    val referralCode: LiveData<String> get() = _referralCode

    fun shareCode() {
        // TODO: Implement actual sharing logic (e.g., using an Intent)
    }
} 