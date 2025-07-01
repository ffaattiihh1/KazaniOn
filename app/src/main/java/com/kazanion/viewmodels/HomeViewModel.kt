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
import kotlinx.coroutines.CoroutineExceptionHandler

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
    
    private var retryCount = 0
    private var lastUsername: String? = null
    private var lastLoadTime: Long = 0
    private var dataLoaded = false
    
    // Cache timeout - 5 dakika
    private val CACHE_TIMEOUT = 5 * 60 * 1000L

    fun loadAllData(username: String, forceRefresh: Boolean = false) {
        Log.d("HomeViewModel", "=== STARTING loadAllData (Retry: $retryCount) ===")
        Log.d("HomeViewModel", "Username: $username, forceRefresh: $forceRefresh")
        
        // Cache kontrolü
        val currentTime = System.currentTimeMillis()
        val cacheValid = dataLoaded && 
                         (currentTime - lastLoadTime) < CACHE_TIMEOUT && 
                         lastUsername == username
        
        if (cacheValid && !forceRefresh) {
            Log.d("HomeViewModel", "✅ Using cached data (${(currentTime - lastLoadTime) / 1000}s ago)")
            return
        }
        
        Log.d("HomeViewModel", "🔄 Loading fresh data from API...")
        lastUsername = username
        
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.e("HomeViewModel", "Coroutine exception: ${exception.message}", exception)
            _error.value = "Beklenmeyen bir hata oluştu: ${exception.message}"
            _isLoading.value = false
        }
        
        viewModelScope.launch(exceptionHandler) {
            _isLoading.value = true
            try {
                Log.d("HomeViewModel", "Making API calls...")
                
                // Her API çağrısını ayrı ayrı yakalayalım
                val storiesResult = try {
                    Log.d("HomeViewModel", "Calling getStories...")
                    apiService.getStories()
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "=== STORIES API ERROR ===")
                    Log.e("HomeViewModel", "Error Type: ${e.javaClass.simpleName}")
                    Log.e("HomeViewModel", "Error Message: ${e.message}")
                    if (e is retrofit2.HttpException) {
                        Log.e("HomeViewModel", "HTTP Status: ${e.code()}")
                    }
                    if (e is java.net.SocketTimeoutException) {
                        Log.e("HomeViewModel", "⚠️ TIMEOUT - Backend probably sleeping!")
                    }
                    emptyList()
                }
                
                val activePollsResult = try {
                    Log.d("HomeViewModel", "Calling getActiveLinkPolls...")
                    apiService.getActiveLinkPolls()
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "=== ACTIVE POLLS API ERROR ===")
                    Log.e("HomeViewModel", "Error Type: ${e.javaClass.simpleName}")
                    Log.e("HomeViewModel", "Error Message: ${e.message}")
                    if (e is retrofit2.HttpException) {
                        Log.e("HomeViewModel", "HTTP Status: ${e.code()}")
                    }
                    if (e is java.net.SocketTimeoutException) {
                        Log.e("HomeViewModel", "⚠️ TIMEOUT - Backend probably sleeping!")
                    }
                    emptyList()
                }
                
                val locationPollsResult = try {
                    Log.d("HomeViewModel", "Calling getLocationBasedPolls...")
                    apiService.getLocationBasedPolls()
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "=== LOCATION POLLS API ERROR ===")
                    Log.e("HomeViewModel", "Error Type: ${e.javaClass.simpleName}")
                    Log.e("HomeViewModel", "Error Message: ${e.message}")
                    if (e is retrofit2.HttpException) {
                        Log.e("HomeViewModel", "HTTP Status: ${e.code()}")
                    }
                    if (e is java.net.SocketTimeoutException) {
                        Log.e("HomeViewModel", "⚠️ TIMEOUT - Backend probably sleeping!")
                    }
                    emptyList()
                }
                
                val userBalanceResult = try {
                    Log.d("HomeViewModel", "Calling getUserBalance for: $username")
                    apiService.getUserBalance(username)
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "=== USER BALANCE API ERROR ===")
                    Log.e("HomeViewModel", "Error Type: ${e.javaClass.simpleName}")
                    Log.e("HomeViewModel", "Error Message: ${e.message}")
                    if (e is retrofit2.HttpException) {
                        Log.e("HomeViewModel", "HTTP Status: ${e.code()}")
                        if (e.code() == 404) {
                            Log.e("HomeViewModel", "User '$username' not found in database")
                        }
                    }
                    if (e is java.net.SocketTimeoutException) {
                        Log.e("HomeViewModel", "⚠️ TIMEOUT - Backend probably sleeping!")
                    }
                    null
                }

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
                retryCount = 0 // Reset retry count on success
                dataLoaded = true
                lastLoadTime = System.currentTimeMillis()

            } catch (e: Exception) {
                Log.e("HomeViewModel", "General error loading data: ${e.message}", e)
                
                // Timeout durumunda otomatik retry
                if (e is java.net.SocketTimeoutException && retryCount < 2) {
                    retryCount++
                    Log.d("HomeViewModel", "🔄 Auto-retrying due to timeout (attempt $retryCount/2)...")
                    _error.value = "Sunucu uyandırılıyor, tekrar deneniyor... ($retryCount/2)"
                    
                    // 3 saniye bekle ve tekrar dene
                    viewModelScope.launch {
                        kotlinx.coroutines.delay(3000)
                        lastUsername?.let { loadAllData(it, forceRefresh) }
                    }
                    return@launch
                }
                
                // Max retry'a ulaşıldı veya başka hata türü
                var errorMessage = when {
                    e is java.net.SocketTimeoutException -> "Sunucu şu anda uyuyor. Lütfen birkaç saniye sonra tekrar deneyin."
                    e is java.net.UnknownHostException -> "İnternet bağlantısını kontrol edin"
                    e is java.net.ConnectException -> "Sunucuya bağlanılamıyor"
                    e is retrofit2.HttpException -> "Sunucu hatası (${e.code()})"
                    else -> "Veri yüklenirken bir hata oluştu: ${e.message}"
                }
                
                _error.value = errorMessage
                retryCount = 0 // Reset retry count
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun retryLoadData() {
        retryCount = 0
        dataLoaded = false // Force refresh
        lastUsername?.let { loadAllData(it, true) }
    }

    fun onErrorShown() {
        _error.value = null
    }
} 