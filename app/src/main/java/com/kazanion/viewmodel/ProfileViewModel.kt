package com.kazanion.viewmodel

import androidx.lifecycle.ViewModel
import com.kazanion.model.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {
    private val _editProfileResult = MutableStateFlow<Result<Unit>>(Result.Loading)
    val editProfileResult: StateFlow<Result<Unit>> = _editProfileResult

    fun editProfile() {
        // TODO: Implement profile editing logic
        _editProfileResult.value = Result.Success(Unit)
    }
} 