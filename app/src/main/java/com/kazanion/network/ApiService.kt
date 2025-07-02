package com.kazanion.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.kazanion.model.Poll
import com.kazanion.model.Achievement
import com.kazanion.model.LeaderboardEntry
import com.kazanion.model.UserRanking
import com.kazanion.models.User
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface ApiService {
    @GET("api/polls")
    suspend fun getAllPolls(): List<Poll>

    @GET("api/polls/active/link")
    suspend fun getActiveLinkPolls(): List<Poll>

    @GET("api/polls/location-based")
    suspend fun getLocationBasedPolls(): List<LocationPoll>

    @GET("api/stories")
    suspend fun getStories(): List<Story>

    @GET("api/users/username/{username}")
    suspend fun getUserBalance(@Path("username") username: String): UserBalance

    @POST("api/users")
    suspend fun createUser(@Body userData: CreateUserRequest): CreateUserResponse

    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") userId: Long, @Body userData: CreateUserRequest): CreateUserResponse

    @POST("api/users/register")
    suspend fun registerUser(@Body user: RegisterUserRequest): User

    @POST("api/users/google-login")
    suspend fun googleLogin(@Body googleData: GoogleLoginRequest): User

    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/users/forgot-password")
    suspend fun forgotPassword(@Body request: Map<String, String>): Map<String, Any>

    @GET("api/users/profile/{userId}")
    suspend fun getUserProfile(@Path("userId") userId: Long): User

    @PUT("api/users/profile/{userId}")
    suspend fun updateUserProfile(@Path("userId") userId: Long, @Body profileData: UpdateProfileRequest): User

    @GET("api/users/{id}/completed-polls")
    suspend fun getCompletedPolls(@Path("id") userId: Long): List<CompletedPoll>

    @GET("api/achievements")
    suspend fun getAllAchievements(): List<Achievement>

    @GET("api/achievements/user/{userId}")
    suspend fun getUserAchievements(@Path("userId") userId: Long): List<Achievement>

    @GET("api/achievements/leaderboard")
    suspend fun getLeaderboard(@Query("limit") limit: Int = 50): List<LeaderboardEntry>

    @GET("api/achievements/leaderboard/user/{userId}")
    suspend fun getUserRanking(@Path("userId") userId: Long): UserRanking

    @GET("api/users/{username}")
    suspend fun getUserByUsername(@Path("username") username: String): User

    @POST("api/polls/{pollId}/complete")
    suspend fun completePoll(@Path("pollId") pollId: Long, @Body request: CompletePollRequest): CompletePollResponse

    @POST("api/achievements/check/{userId}")
    suspend fun checkAchievements(@Path("userId") userId: Long): Map<String, String>

    @PUT("api/users/{userId}/fcm-token")
    suspend fun updateFcmToken(@Path("userId") userId: Long, @Body tokenData: Map<String, String>): retrofit2.Response<Map<String, String>>

    @GET("api/notifications/user/{userId}/unread")
    suspend fun getUserUnreadNotifications(@Path("userId") userId: Long): retrofit2.Response<Map<String, Any>>

    companion object {
        // Production ve Development URL'leri
        private const val PRODUCTION_URL = "https://kazanion.onrender.com/"
        private const val LOCAL_DEV_URL = "http://10.0.2.2:8081/"
        private const val BASE_URL = PRODUCTION_URL  // Production için

        fun create(): ApiService {
            // Production için her zaman production URL kullan
            val selectedUrl = BASE_URL
            
            // Debug: Hangi URL kullanıldığını logla
            android.util.Log.d("ApiService", "📱 Device: ${android.os.Build.MODEL}")
            android.util.Log.d("ApiService", "🌐 Production URL: $selectedUrl")
            
            val gson = GsonBuilder()
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
                .create()

            val okHttpClient = okhttp3.OkHttpClient.Builder()
                .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)  // Daha uzun timeout
                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)     // Daha uzun timeout
                .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)    // Daha uzun timeout
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Connection", "close")    // Connection reuse'u önle
                        .addHeader("Cache-Control", "no-cache") // Cache sorunlarını önle
                        .addHeader("Accept", "application/json")
                        .build()
                    chain.proceed(request)
                }
                .build()

            return Retrofit.Builder()
                .baseUrl(selectedUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ApiService::class.java)
        }

    }
}

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime {
        return LocalDateTime.parse(json?.asString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
}

data class RegisterUserRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null
)

data class GoogleLoginRequest(
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val googleId: String? = null
)

data class UpdateProfileRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val username: String? = null
)

data class CompletedPoll(
    val id: Long,
    val userId: Long,
    val pollId: Long,
    val completedAt: LocalDateTime
)

data class CompletePollRequest(
    val userId: Long
)

data class CompletePollResponse(
    val success: Boolean,
    val pointsEarned: Int,
    val message: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val user: User?,
    val token: String?,
    val message: String
)

data class CreateUserRequest(
    val username: String,
    val email: String,
    val passwordHash: String,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val birthDate: String?,
    val points: Int,
    val balance: Double,
    val city: String? = null
)

data class CreateUserResponse(
    val success: Boolean? = true,
    val message: String? = null,
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val birthDate: String?,
    val publicId: String? = null,  // 6 haneli public ID
    val points: Int,
    val balance: Double,
    val city: String? = null
) 