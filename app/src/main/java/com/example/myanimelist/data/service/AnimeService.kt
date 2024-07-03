package com.example.myanimelist.data.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AnimeService {

    companion object  {
        val BASE_URL = "https://docs.api.jikan.moe/"

        fun create(): AnimeService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(AnimeService::class.java)
        }
    }
}