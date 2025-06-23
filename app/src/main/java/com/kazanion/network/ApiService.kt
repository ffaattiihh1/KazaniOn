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

    @POST("api/users/register")
    suspend fun registerUser(@Body user: RegisterUserRequest): User

    @POST("api/users/google-login")
    suspend fun googleLogin(@Body googleData: GoogleLoginRequest): User

    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

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
    suspend fun getLeaderboard(): List<LeaderboardEntry>

    @GET("api/users/{username}")
    suspend fun getUserByUsername(@Path("username") username: String): User

    @POST("api/polls/{pollId}/complete")
    suspend fun completePoll(@Path("pollId") pollId: Long, @Body request: CompletePollRequest): CompletePollResponse

    @POST("api/achievements/check/{userId}")
    suspend fun checkAchievements(@Path("userId") userId: Long): Map<String, String>

    companion object {
        private const val BASE_URL = "http://192.168.1.105:8081/"

        fun create(): ApiService {
            val gson = GsonBuilder()
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
                .create()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
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