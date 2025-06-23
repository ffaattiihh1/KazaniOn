package com.kazanion.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InviteViewModel : ViewModel() {
    fun copyInviteLinkToClipboard(context: Context) {
        // TODO: Implement clipboard copy logic
    }

    private val _inviteLink = MutableLiveData<String>()
    val inviteLink: LiveData<String> = _inviteLink

    init {
        // TODO: Generate actual invite link
        _inviteLink.value = "https://kazanion.com/invite/123456"
    }
} 