// app/src/main/java/com/example/musicapp/network/RetrofitInstance.kt
package com.example.musicapp.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            // Таймаут на установление соединения
            .connectTimeout(15, TimeUnit.SECONDS)
            // Таймаут на чтение ответа
            .readTimeout(30, TimeUnit.SECONDS)
            // Таймаут на запись запроса
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val api: AudiusService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.audius.co/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AudiusService::class.java)
    }
}
