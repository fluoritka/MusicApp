package com.example.musicapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: AudiusService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.audius.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AudiusService::class.java)
    }
}
