package com.kazanion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WalletViewModel : ViewModel() {
    private val _totalPoints = MutableLiveData<Int>(0)
    val totalPoints: LiveData<Int> get() = _totalPoints

    private val _estimatedValue = MutableLiveData<Double>(0.0)
    val estimatedValue: LiveData<Double> get() = _estimatedValue

    init {
        // Load dummy data for wallet
        loadWalletData()
    }

    private fun loadWalletData() {
        // Simulate fetching data
        _totalPoints.value = 150
        _estimatedValue.value = 7.50
    }

    fun requestGiftVoucher() {
        // TODO: Implement gift voucher request logic
    }

    fun requestCashOut() {
        // TODO: Implement cash out logic
    }
} 