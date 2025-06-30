package com.kazanion.viewmodels

import android.util.Log
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
        Log.d("HomeViewModel", "=== STARTING loadAllData ===")
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("HomeViewModel", "Making API calls...")
                
                // Paralel olarak tüm istekleri çalıştır
                val storiesDeferred = async { 
                    Log.d("HomeViewModel", "Calling getStories...")
                    apiService.getStories() 
                }
                val activePollsDeferred = async { 
                    Log.d("HomeViewModel", "Calling getActiveLinkPolls...")
                    apiService.getActiveLinkPolls() 
                }
                val locationPollsDeferred = async { 
                    Log.d("HomeViewModel", "Calling getLocationBasedPolls...")
                    apiService.getLocationBasedPolls() 
                }
                val userBalanceDeferred = async { 
                    Log.d("HomeViewModel", "Calling getUserBalance...")
                    apiService.getUserBalance(username) 
                }

                // Tüm sonuçları bekle
                val storiesResult = storiesDeferred.await()
                val activePollsResult = activePollsDeferred.await()
                val locationPollsResult = locationPollsDeferred.await()
                val userBalanceResult = userBalanceDeferred.await()

                // Log results
                Log.d("HomeViewModel", "Stories count: ${storiesResult.size}")
                Log.d("HomeViewModel", "Active polls count: ${activePollsResult.size}")
                Log.d("HomeViewModel", "Location polls count: ${locationPollsResult.size}")
                Log.d("HomeViewModel", "User balance: $userBalanceResult")
                
                // Log active polls details
                activePollsResult.forEachIndexed { index, poll ->
                    Log.d("HomeViewModel", "Active Poll $index: ${poll.title} - Link: ${poll.link}")
                }

                // LiveData'ları güncelle
                _stories.value = storiesResult
                _activePolls.value = activePollsResult
                _locationPolls.value = locationPollsResult
                _userBalance.value = userBalanceResult

                Log.d("HomeViewModel", "=== Data loaded successfully ===")

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading data: ${e.message}", e)
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