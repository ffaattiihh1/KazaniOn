package com.kazanion.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazanion.model.Achievement
import com.kazanion.model.LeaderboardEntry
import com.kazanion.model.UserRanking
import com.kazanion.network.ApiService
import kotlinx.coroutines.launch

class AchievementViewModel : ViewModel() {
    private val apiService = ApiService.create()
    
    private val _achievements = MutableLiveData<List<Achievement>>()
    val achievements: LiveData<List<Achievement>> = _achievements
    
    private val _userAchievements = MutableLiveData<List<Achievement>>()
    val userAchievements: LiveData<List<Achievement>> = _userAchievements
    
    private val _leaderboard = MutableLiveData<List<LeaderboardEntry>>()
    val leaderboard: LiveData<List<LeaderboardEntry>> = _leaderboard
    
    private val _userRanking = MutableLiveData<UserRanking?>()
    val userRanking: LiveData<UserRanking?> = _userRanking
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadAllAchievements() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("AchievementViewModel", "Loading all achievements...")
                
                val achievementsList = apiService.getAllAchievements()
                _achievements.value = achievementsList
                
                Log.d("AchievementViewModel", "Loaded ${achievementsList.size} achievements")
            } catch (e: Exception) {
                Log.e("AchievementViewModel", "Error loading achievements: ${e.message}", e)
                _error.value = "Başarımlar yüklenirken hata oluştu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserAchievements(userId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("AchievementViewModel", "Loading user achievements for userId: $userId")
                
                val userAchievementsList = apiService.getUserAchievements(userId)
                _userAchievements.value = userAchievementsList
                
                Log.d("AchievementViewModel", "User has ${userAchievementsList.size} achievements")
            } catch (e: Exception) {
                Log.e("AchievementViewModel", "Error loading user achievements: ${e.message}", e)
                _error.value = "Kullanıcı başarımları yüklenirken hata oluştu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadLeaderboard(limit: Int = 50) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("AchievementViewModel", "Loading leaderboard with limit: $limit")
                
                val leaderboardList = apiService.getLeaderboard(limit)
                _leaderboard.value = leaderboardList
                
                Log.d("AchievementViewModel", "Loaded ${leaderboardList.size} leaderboard entries")
                leaderboardList.forEachIndexed { index, entry ->
                    Log.d("AchievementViewModel", "Rank ${entry.rank}: ${entry.displayName} - ${entry.points} points, badge: ${entry.badge}")
                }
            } catch (e: Exception) {
                Log.e("AchievementViewModel", "Error loading leaderboard: ${e.message}", e)
                _error.value = "Sıralama yüklenirken hata oluştu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserRanking(userId: Long) {
        viewModelScope.launch {
            try {
                Log.d("AchievementViewModel", "Loading user ranking for userId: $userId")
                
                val ranking = apiService.getUserRanking(userId)
                _userRanking.value = ranking
                
                Log.d("AchievementViewModel", "User rank: ${ranking.rank}/${ranking.totalUsers}, badge: ${ranking.badge}")
            } catch (e: Exception) {
                Log.e("AchievementViewModel", "Error loading user ranking: ${e.message}", e)
                if (e is retrofit2.HttpException && e.code() == 404) {
                    Log.d("AchievementViewModel", "User not found in rankings")
                    _userRanking.value = null
                } else {
                    _error.value = "Kullanıcı sıralaması yüklenirken hata oluştu: ${e.message}"
                }
            }
        }
    }

    fun getUserBadge(): String? {
        return _userRanking.value?.badge
    }
    
    fun getUserRank(): Int? {
        return _userRanking.value?.rank
    }
    
    fun getTotalUsers(): Int? {
        return _userRanking.value?.totalUsers
    }

    fun onErrorShown() {
        _error.value = null
    }
} 