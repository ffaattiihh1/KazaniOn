package com.kazanion.utils

import com.kazanion.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    
    private const val BASE_URL = "https://kazanion.onrender.com/"
    
    private var retrofit: Retrofit? = null
    
    fun getRetrofitInstance(): Retrofit {
        if (retrofit == null) {
            val okHttpClient = okhttp3.OkHttpClient.Builder()
                .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Connection", "close")
                        .addHeader("Cache-Control", "no-cache")
                        .addHeader("Accept", "application/json")
                        .build()
                    chain.proceed(request)
                }
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
} 