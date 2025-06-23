package com.kazanion.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazanion.model.Poll
import com.kazanion.network.ApiService
import com.kazanion.network.LocationPoll
import com.kazanion.network.Story
import com.kazanion.network.UserBalance
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val apiService = ApiService.create()

    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> = _stories

    private val _activePolls = MutableLiveData<List<Poll>>()
    val activePolls: LiveData<List<Poll>> = _activePolls

    private val _locationPolls = MutableLiveData<List<LocationPoll>>()
    val locationPolls: LiveData<List<LocationPoll>> = _locationPolls

    private val _userBalance = MutableLiveData<UserBalance>()
    val userBalance: LiveData<UserBalance> = _userBalance

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadAllData(username: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Paralel olarak tüm istekleri çalıştır
                val storiesDeferred = async { apiService.getStories() }
                val activePollsDeferred = async { apiService.getActiveLinkPolls() }
                val locationPollsDeferred = async { apiService.getLocationBasedPolls() }
                val userBalanceDeferred = async { apiService.getUserBalance(username) }

                // Tüm sonuçları bekle
                val storiesResult = storiesDeferred.await()
                val activePollsResult = activePollsDeferred.await()
                val locationPollsResult = locationPollsDeferred.await()
                val userBalanceResult = userBalanceDeferred.await()

                // LiveData'ları güncelle
                _stories.value = storiesResult
                _activePolls.value = activePollsResult
                _locationPolls.value = locationPollsResult
                _userBalance.value = userBalanceResult

            } catch (e: Exception) {
                _error.value = "Veri yüklenirken bir hata oluştu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onErrorShown() {
        _error.value = null
    }
} 