package com.example.myanimelist.data.service


import com.example.myanimelist.domain.model2.SearchedAnime
import com.example.myanimelist.domain.model2.TopAnime
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AnimeService {

    @GET("top/anime")
    fun getTopAnime(): Call<TopAnime>

    @GET("anime")
    fun getSearchedAnime(@Query("q")queryString: String): Call<SearchedAnime>

    companion object  {
        const val BASE_URL = "https://docs.api.jikan.moe/"

        fun create(): AnimeService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(AnimeService::class.java)
        }
    }
}