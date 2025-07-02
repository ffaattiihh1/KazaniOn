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
    
    private var lastUsername: String? = null
    private var lastLoadTime: Long = 0
    private var dataLoaded = false
    
    // Cache timeout - 30 saniye (SÜPER AGRESİF!)
    private val CACHE_TIMEOUT = 30 * 1000L

    fun loadAllData(username: String, forceRefresh: Boolean = false) {
        // Cache kontrolü
        val currentTime = System.currentTimeMillis()
        val cacheValid = dataLoaded && 
                         (currentTime - lastLoadTime) < CACHE_TIMEOUT && 
                         lastUsername == username
        
        if (cacheValid && !forceRefresh) {
            Log.d("HomeViewModel", "✅ Cache kullanılıyor")
            return
        }
        
        lastUsername = username
        
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.e("HomeViewModel", "Hata: ${exception.message}")
            _error.value = "Bağlantı hatası - Yeniden deneyin"
            _isLoading.value = false
        }
        
        viewModelScope.launch(exceptionHandler) {
            _isLoading.value = true
            try {
                Log.d("HomeViewModel", "🚀 ÖNCELİKLİ YÜKLEME - Önce anketler!")
                
                // 1. ÖNCE ANKETLERİ YÜKLEYELİM (en önemli!)
                val activePollsDeferred = async { 
                    try { 
                        Log.d("HomeViewModel", "📋 ÖNCE: Active polls yükleniyor...")
                        val polls = apiService.getActiveLinkPolls()
                        Log.d("HomeViewModel", "✅ Active polls yüklendi: ${polls.size}")
                        polls
                    } catch (e: Exception) { 
                        Log.e("HomeViewModel", "❌ Active polls HATA: ${e.message}")
                        emptyList<Poll>() 
                    }
                }
                
                val locationPollsDeferred = async { 
                    try { 
                        Log.d("HomeViewModel", "📍 ÖNCE: Location polls yükleniyor...")
                        val polls = apiService.getLocationBasedPolls()
                        Log.d("HomeViewModel", "✅ Location polls yüklendi: ${polls.size}")
                        polls
                    } catch (e: Exception) { 
                        Log.e("HomeViewModel", "❌ Location polls HATA: ${e.message}")
                        emptyList<LocationPoll>() 
                    }
                }

                // Anketleri hemen bekle ve göster
                val activePollsResult = activePollsDeferred.await()
                val locationPollsResult = locationPollsDeferred.await()
                
                // ANKETLER HEMEN GÖRÜNÜR!
                _activePolls.value = activePollsResult
                _locationPolls.value = locationPollsResult
                
                Log.d("HomeViewModel", "🎯 ANKETLERİN İLKİ YÜKLENDİ! Kullanıcı artık anketleri görebilir")
                
                // 2. SONRA DİĞER VERİLERİ YÜKLEYELİM (background'da)
                val storiesDeferred = async { 
                    try { 
                        apiService.getStories()
                    } catch (e: Exception) { 
                        Log.w("HomeViewModel", "Stories hata: ${e.message}")
                        emptyList<Story>() 
                    }
                }
                
                val userBalanceDeferred = async { 
                    try { 
                        apiService.getUserBalance(username)
                    } catch (e: Exception) { 
                        Log.w("HomeViewModel", "User balance hata: ${e.message}")
                        null 
                    }
                }

                // Diğer verileri bekle
                val storiesResult = storiesDeferred.await()
                val userBalanceResult = userBalanceDeferred.await()

                // Diğer verileri güncelle
                _stories.value = storiesResult
                _userBalance.value = userBalanceResult

                dataLoaded = true
                lastLoadTime = System.currentTimeMillis()
                
                val totalPolls = activePollsResult.size + locationPollsResult.size
                Log.d("HomeViewModel", "✅ TÜM VERİLER YÜKLENDİ! Toplam ${totalPolls} anket gösteriliyor")
                
                if (totalPolls == 0) {
                    _error.value = "❌ Hiç anket bulunamadı!"
                }

            } catch (e: Exception) {
                Log.e("HomeViewModel", "GENEL HATA: ${e.message}")
                _error.value = when {
                    e.message?.contains("timeout") == true -> "⏱️ Sunucu çok yavaş"
                    e.message?.contains("connection") == true -> "🔌 Bağlantı hatası"
                    else -> "❌ Anketler yüklenemedi"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun retryLoadData() {
        dataLoaded = false // Cache'i temizle
        lastUsername?.let { loadAllData(it, true) }
    }

    fun onErrorShown() {
        _error.value = null
    }
} 