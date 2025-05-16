package com.example.musicapp.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Singleton для настройки Retrofit и HTTP-клиента
object RetrofitInstance {

    // HTTP клиент с настройками таймаутов
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS) // время ожидания установления соединения
            .readTimeout(30, TimeUnit.SECONDS)    // время ожидания получения ответа
            .writeTimeout(30, TimeUnit.SECONDS)   // время ожидания отправки запроса
            .build()
    }

    // Экземпляр API для выполнения сетевых запросов через интерфейс AudiusService
    val api: AudiusService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.audius.co/")              // базовый URL сервиса Audius
            .client(okHttpClient)                             // подключаем сконфигурированный HTTP клиент
            .addConverterFactory(GsonConverterFactory.create()) // конвертер для работы с JSON
            .build()
            .create(AudiusService::class.java)                // создаём реализацию интерфейса API
    }
}
